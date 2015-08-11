package com.mygdx.game;

import com.badlogic.gdx.*;

/**
 * Created by Vanessa on 03/08/2015.
 */
public abstract class TelaBase implements Screen {

    protected Game game;

    public TelaBase(Game game){
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }
}
