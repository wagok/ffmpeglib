package com.wagok.ffmpeg;




/**
 * Created with IntelliJ IDEA.
 * User: wlad
 * Date: 6/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FFmpegVideoEditor   {

    public void getInfo() {

        return;
    }

    public void trimVideo() {

    }

    public void joinVideo(String[] files) {

    }


    public void command(String[] cmdLine) {
        Videokit vk = new Videokit();
        vk.run(cmdLine);
    }




}
