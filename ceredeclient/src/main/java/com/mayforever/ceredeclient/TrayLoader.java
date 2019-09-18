/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.CacheUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author mis
 */
public class TrayLoader 
    implements ActionListener{
    
    private String PATH_ICON = "/home" + File.separator + "mis"+ File.separator+ 
            "ceredeclient"+File.separator + "icon"+ File.separator +"c32.png";
    private String OPEN_CEREDE_LABEL = "Open Cerede";
    private MenuItem menuItemOpenCerede = null;
    private String TITLE = "Cerede";

    
    public TrayLoader(){
        SystemTray systemTray = null;
        SystemTray.DEBUG = true; 
        CacheUtil.clear(); 
        systemTray = SystemTray.get();
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.setTooltip("Cerede");
        systemTray.setImage(PATH_ICON);
        // systemTray.setStatus("No Mail");
        Menu mainMenu = systemTray.getMenu();
        
        menuItemOpenCerede = new MenuItem(OPEN_CEREDE_LABEL, this);
        
        mainMenu.add(menuItemOpenCerede);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(ae.getSource() == menuItemOpenCerede){
            App.ceredeForm.showCeredeForm();
        }
    }
}
