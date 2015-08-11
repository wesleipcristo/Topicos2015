package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;


/**
 * Created by Vanessa on 03/08/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private BitmapFont fonte;
    private Label lblpontuacao;
    private Image jogador;
    private Texture texturaJogador;
    private Texture texturaJogadordireita;
    private Texture texturaJogadoresquerda;
    private boolean indoDireita;
    private boolean indoEsquerda;
    private boolean indoCima;
    private boolean indoBaixo;



    /**
     * Construtor padrao da tela de jogo
     * @param game Referencia a classe principal
     */
    public TelaJogo(Game game){
        super(game);
    }

    /**
     * Chamado quando a tela é exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight,camera));

        initFonte();
        initInformacoes();
        initiJoador();
    }

    private void initiJoador() {
        texturaJogador = new Texture("sprites/player.png");
        texturaJogadordireita = new Texture("sprites/player-right.png");
        texturaJogadoresquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturaJogador);
        jogador.setPosition(camera.viewportWidth / 2 - jogador.getWidth() / 2, 15);
        palco.addActor(jogador);
    }

    private void initInformacoes() {
        Label.LabelStyle lblEstilo = new Label.LabelStyle();
        lblEstilo.font = fonte;
        lblEstilo.fontColor = Color.WHITE;

        lblpontuacao = new Label("0 pontos", lblEstilo);
        palco.addActor(lblpontuacao);
    }

    private void initFonte(){
        fonte = new BitmapFont();
    }


    /**
     * Chamado a todo quadro de atualizacao do jogo (FPS)
     * @param delta tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.0f,.0f,.1f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lblpontuacao.setPosition(10, camera.viewportHeight - 20);
        capituraTeclas();
        atualizarJogador(delta);

        palco.act(delta);
        palco.draw();
    }

    /**
     * atualiza a posição do jogador
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; // velocidade do movimento do jogador
        if (indoDireita && (jogador.getX() < camera.viewportWidth - jogador.getImageWidth())) {
            float x = jogador.getX() + velocidade * delta;
            float y = jogador.getY();
            jogador.setPosition(x,y);
        }
        if (indoEsquerda && (jogador.getX() > 0)) {
            float x = jogador.getX() - velocidade * delta;
            float y = jogador.getY();
            jogador.setPosition(x,y);
        }
        if (indoCima && jogador.getY() < camera.viewportHeight - jogador.getImageHeight()) {
            float x = jogador.getX();
            float y = jogador.getY() + velocidade * delta;
            jogador.setPosition(x,y);
        }
        if (indoBaixo && jogador.getY() > 0) {
            float x = jogador.getX();
            float y = jogador.getY() - velocidade * delta;
            jogador.setPosition(x,y);
        }

        if (indoDireita) {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadordireita)));
        }
        else if (indoEsquerda) {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadoresquerda)));
        } else {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
        }
    }

    /**
     * verifica se as teclas estao pressionadas
     */
    private void capituraTeclas() {
        indoDireita = false;
        indoEsquerda = false;
        indoCima = false;
        indoBaixo = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            indoCima = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            indoBaixo = true;
        }
    }

    /**
     * É chamado sempre que ha uma alteracao no tamanho da tela
     * @param width largura nova
     * @param height altura nova
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * É chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * É chamado sempre que o jogo voltar ao primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * É chamdo quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        fonte.dispose();
        texturaJogador.dispose();
        texturaJogadordireita.dispose();
        texturaJogadoresquerda.dispose();
    }
}
