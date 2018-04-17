package com.example.root.musclediary;

import android.app.Application;

import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.android.manager.ShimmerBluetoothManagerAndroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Handler;

import com.example.root.musclediary.DBHelper;

/**
 * Created by root on 4/10/18.
 */

public class MyGlobals implements Serializable {
    public static ShimmerBluetoothManagerAndroid btManagerGlobal;
    //Write to CSV variables
    public static FileOutputStream outputStreamGlobal;
    public static String macAddresGlobal;
    public boolean firstTimeWrite = true;
    public static String stateShimmer2 = "";

    public static FileWriter fwglobal;
    public static BufferedWriter bwglobal;
    public static int counterdb = 0;
    public static String totalstring = "";
    public static DBHelper mydb;
    public static Shimmer shimmer;

    public MyGlobals() {
    }

    public void setState(String state) {
        if (!state.equals("") && (this.stateShimmer2.equals("") || this.stateShimmer2!=state))
            this.stateShimmer2 = new String(state);
    }

    public void resetParameters() {

    }

    public String getStateShimmer() {
        return this.stateShimmer2;
    }

    public void setBTHandler(Context c, Handler myHandler) {
        mydb = new DBHelper(c);
        try {
            this.btManagerGlobal = new ShimmerBluetoothManagerAndroid(c, myHandler);
            //btManager.connectShimmerThroughBTAddress(bluetoothAdd);
            //shimmer = new Shimmer(mHandler);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ManualDeb: MyGlobals Problem connecting");
        }
    }

    public void setBT(String add) {
        this.macAddresGlobal = add;
        this.btManagerGlobal.connectShimmerThroughBTAddress(add);
    }

    /*
    * Not used
     */
    public void setCVS(File file4) {
        /*try {
            if (!file4.exists()) {
                file4.createNewFile();
            }
            this.outputStreamGlobal = new FileOutputStream(file4, true);
            System.out.println("ManualDeb: set file");
        } catch (Exception e) {
            this.outputStreamGlobal = null;
            System.out.println("ManualDeb: set file wrong");
            e.printStackTrace();
        }*/
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "9ObjectClusterExample Data " + DateFormat.getDateTimeInstance().format(new Date()) + ".csv";
        String filePath = baseDir + File.separator + fileName;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            this.fwglobal = new FileWriter(file.getAbsoluteFile());
            this.bwglobal = new BufferedWriter(this.fwglobal);
            System.out.println("ManualDeb: set file");
        } catch (Exception e) {
            System.out.println("ManualDeb: set file wrong");
            e.printStackTrace();
        }
        /*if (counterdb>=1000) {
            mydb.insertContact(totalstring);
            totalstring = "";
            counterdb = 0;
        }
        else {
            totalstring += "";
            counterdb = 0;
        }*/

    }

    public void startBT() {
        try {   //Stop CSV writing
            shimmer = (Shimmer) btManagerGlobal.getShimmer(macAddresGlobal);
            shimmer.enablePCTimeStamps(false);
            shimmer.enableArraysDataStructure(true);
            btManagerGlobal.startStreaming(macAddresGlobal);
            System.out.println("ManualDeb: Start writing");
        } catch (Exception e) {
            System.out.println("ManualDeb: MyGlobals startBT");
        }
    }

    public void stopBT() {
        try {   //Stop CSV writing
            this.bwglobal.flush();
            this.bwglobal.close();
            this.fwglobal.close();
            this.firstTimeWrite = true;
            System.out.println("ManualDeb: Stopped writing");
        } catch (Exception e) {
            System.out.println("ManualDeb: Error stopBT flush");
        }
        try {
            btManagerGlobal.stopStreaming(macAddresGlobal);
            this.shimmer.stop();
            System.out.println("ManualDeb: Stopped writing");
        } catch (Exception e) {
            System.out.println("ManualDeb: Error stopBT");
        }
    }

    public ShimmerBluetoothManagerAndroid getBT() {
        return btManagerGlobal;
    }

    public FileOutputStream getCSV() {
        return outputStreamGlobal;
    }

    public void writeCSV(String channelName) {
        try {
            //outputStreamGlobal.write(channelName.getBytes());
            this.bwglobal.write(channelName);
        } catch (IOException e) {
            //System.out.println("ManualDeb: MyGlobals error write "+e.getMessage());
            e.printStackTrace();
            System.out.println("ManualDeb: Error writing file");
        }

    }

    public void setfirstTimeWrite(boolean bl) {
        firstTimeWrite = bl;
    }

    public boolean getfirstTimeWrite() {
        return firstTimeWrite;
    }
}
