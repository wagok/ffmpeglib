package com.wagok.ffmpeg.interfaces;

import com.wagok.ffmpeg.VideoException;

import java.util.Date;

public abstract class AbstractVideoEditor implements IVideoEditor  {

    @Override
    public String save() throws VideoException {
        String name = getInfo().getFileName();
        return save(new Date().getTime() +name);
    }

}
