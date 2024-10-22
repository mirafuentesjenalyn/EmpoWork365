package EmpoWork365;

import javax.swing.ImageIcon;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jenal
 */
public class IconLoader {
    private static final String ICON_PATH = "icon/empowork_logo.png"; 

    // Method to load and return the icon
    public static ImageIcon getIcon() {
        return new ImageIcon(IconLoader.class.getResource("/" + ICON_PATH));
    }
}

