/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.compute.samples;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.Disk;
import com.microsoft.azure.management.compute.KnownLinuxVirtualMachineImage;
import com.microsoft.azure.management.compute.KnownWindowsVirtualMachineImage;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.model.Creatable;
import com.microsoft.azure.management.samples.Utils;
import com.microsoft.rest.LogLevel;
import com.microsoft.azure.Page;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.locks.ManagementLock;
import com.microsoft.azure.management.locks.ManagementLocks;

import java.io.File;
import java.util.Date;

/**
 * Azure Compute sample for managing virtual machines -
 *  - Create a virtual machine with managed OS Disk
 *  - Start a virtual machine
 *  - Stop a virtual machine
 *  - Restart a virtual machine
 *  - Update a virtual machine
 *    - Tag a virtual machine (there are many possible variations here)
 *    - Attach data disks
 *    - Detach data disks
 *  - List virtual machines
 *  - Delete a virtual machine.
 */
public final class ManageVirtualMachine {

    /**
     * Main function which runs the actual sample.
     * @param azure instance of the azure client
     * @return true if sample runs successfully
     */
    public static boolean runSample(Azure azure) {
        final Region region = Region.US_WEST2;
        final String windowsVMName = Utils.createRandomName("mbwindowsVMa");
        final String linuxVMName = Utils.createRandomName("mblinuxVMa");
        final String rgName = Utils.createRandomName("mbVMResGrp1");
        final String userName = "mibelhe";
        // [SuppressMessage("Microsoft.Security", "CS002:SecretInNextLine", Justification="Serves as an example, not for deployment. Please change when using this in your code.")]
        final String password = "abc123456789###";

        try {

            //=============================================================
            // Create a Linux VM in the same virtual network

            System.out.println("Creating a Linux VM in the network");
            // CHANGE sub below *****************************************************
            String imageid="/subscriptions/<sub>/resourceGroups/ibLinuxGalleryRG/providers/Microsoft.Compute/galleries/myIbGallery/images/myIbImageDef/versions/0.24537.19969";

            VirtualMachine linuxVM = azure.virtualMachines()
                    .define(linuxVMName)
                        .withRegion(region)
                        .withNewResourceGroup(rgName)
                        .withNewPrimaryNetwork("10.0.0.0/24")
                        .withPrimaryPrivateIPAddressDynamic()
                        .withNewPrimaryPublicIPAddress(linuxVMName+"publicIp")
                        .withLinuxCustomImage(imageid)
                        .withRootUsername(userName)
                        .withRootPassword(password)
                        .withSize(VirtualMachineSizeTypes.STANDARD_B2MS)
                        .create();

            System.out.println("Created a Linux VM (in the same virtual network): " + linuxVM.id());
            Utils.print(linuxVM);

            return true;
        } catch (Exception f) {

            System.out.println(f.getMessage());
            f.printStackTrace();

        } finally {
            System.out.println("finally");
        }
       return false;
    }

    public static boolean runSample2(Azure azure) {
        try {
            System.out.println("-------------------------------Starting VM loop ------------------------------------");
            final PagedList<VirtualMachine> vms = azure.virtualMachines()
                    .list();
            for (VirtualMachine vm : vms) {
                System.out.println("-----------------VM id============="+ vm.id());
                long startTime = System.currentTimeMillis();
                System.out.println("-----------------Starttime---------"+startTime);
                PagedList<ManagementLock> instanceLocks = azure.managementLocks()
                        .listForResource(vm.id());
                System.out.println(vm.id() + "--------------------------:" + (System.currentTimeMillis() - startTime));
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
       return false;
    }


    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {

            //=============================================================
            // Authenticate

            final File credFile = new File(System.getenv("AZURE_AUTH_LOCATION"));

            Azure azure = Azure.configure()
                    .withLogLevel(LogLevel.BODY_AND_HEADERS)
                    .authenticate(credFile)
                    .withDefaultSubscription();

            // Print selected subscription
            System.out.println("Selected subscription: " + azure.subscriptionId());

            runSample(azure);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private ManageVirtualMachine() {

    }
}
