package model;

import controller.Main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class Sound {
    public static void play(String filepath) {
        try {
            if(Main.setting[4].equals("1")){
                URL url = Sound.class.getResource(filepath);
                String urls=url.toString();
                urls=urls.replaceFirst("file:/", "file:///");
                AudioClip ac = Applet.newAudioClip(new URL(urls));
                ac.play();
            }
        } catch (Exception e) {

        }
    }
}
