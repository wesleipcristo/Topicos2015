package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Vanessa on 31/08/2015.
 */
public class Explosao {

    private static float tempo_troca = 1f / 17;

    private int estagio = 0; //Controla o estagio de 0 a 16;
    private Array<Texture> texturas;
    private Image ator;
    private float tempoAcumulado = 0;

    public Explosao(Image ator, Array<Texture> texturas) {
        this.ator = ator;
        this.texturas = texturas;
    }

    public void atualizar (float delta){
        tempoAcumulado += delta;
        if (tempoAcumulado >= tempo_troca){
            tempoAcumulado = 0;
            estagio++;
            Texture textura = texturas.get(estagio);
            ator.setDrawable(new SpriteDrawable(new Sprite(textura)));
        }
    }

    public Image getAtor() {
        return ator;
    }

    public int getEstagio(){
        return estagio;
    }

}
