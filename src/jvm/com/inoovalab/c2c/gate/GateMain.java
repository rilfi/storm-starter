package com.inoovalab.c2c.gate;

import gate.Gate;
import gate.util.GateException;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by root on 2/22/17.
 */
public class GateMain {
    public static void main(String[] args) {

            try {
                Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
                System.out.println(Gate.isInitialised());
                Gate.init();
                System.out.println(Gate.isInitialised());
            } catch (GateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                try {
                    Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } catch (GateException e) {
                e.printStackTrace();
            }
        while (true){

        }
        // Gate.

        }

}
