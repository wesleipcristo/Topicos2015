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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
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
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoro1 = new Array<Image>();
    private Array<Image> meteoro2 = new Array<Image>();




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

        initTexturas();
        initFonte();
        initInformacoes();
        initiJoador();
    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");
    }

    /**
     * Instancia os Objetos do Jogador e Adiciona no palco.
      */
    private void initiJoador() {
        texturaJogador = new Texture("sprites/player.png");
        texturaJogadordireita = new Texture("sprites/player-right.png");
        texturaJogadoresquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturaJogador);
        jogador.setPosition(camera.viewportWidth / 2 - jogador.getWidth() / 2, 15);
        palco.addActor(jogador);
    }

    /**
     * Instancia as informações escritas na tela.
     */
    private void initInformacoes() {
        Label.LabelStyle lblEstilo = new Label.LabelStyle();
        lblEstilo.font = fonte;
        lblEstilo.fontColor = Color.WHITE;

        lblpontuacao = new Label("0 pontos", lblEstilo);
        palco.addActor(lblpontuacao);
    }

    /**
     * Instancia os objetos de fonte.
     */
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
        atualizarTiros(delta);
        capituraTeclas();
        atualizarJogador(delta);
        atualizarMeteoros(delta);


        palco.act(delta);
        palco.draw();

    }

    private float intervaloMeteoro = 100;

    private void atualizarMeteoros(float delta) {
        int tipo = MathUtils.random(1,3);
        intervaloMeteoro = intervaloMeteoro + delta * 70;
        if (tipo == 1 && intervaloMeteoro >= 100) {
            // criar meteoro1
            Image meteoro = new Image(texturaMeteoro1);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getImageWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x,y);
            meteoro1.add(meteoro);
            palco.addActor(meteoro);
            intervaloMeteoro = MathUtils.random(1,100);
        }else if (tipo == 2 && intervaloMeteoro >= 100){
            // criar meteoro2
            Image meteoro = new Image(texturaMeteoro2);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getImageWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x,y);
            meteoro2.add(meteoro);
            palco.addActor(meteoro);
            intervaloMeteoro = MathUtils.random(1,100);
        }
        float velocidade = 100;
        for (Image meteoro : meteoro2) {
            float x = meteoro.getX();
            float y = meteoro.getY() - (MathUtils.random(velocidade + 10, velocidade - 10) + 50) * delta;
            meteoro.setPosition(x,y);
        }
        for (Image meteoro : meteoro1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - MathUtils.random(velocidade + 10, velocidade - 10) * delta;
            meteoro.setPosition(x,y);
        }
    }

    private final float MIN_INTERVALO_TIROS = 0.1f; // Minimo tempo entre tiros.
    private float intervaloTiros = 1000; // Tempo acumulado entre os tiros

    private void atualizarTiros(float delta) {
        intervaloTiros = intervaloTiros + delta; // Acumula o tempo percorrido
        if (atirando && intervaloTiros >= MIN_INTERVALO_TIROS) { // Verifica se esta atirando e se o tempo minimo foi ultrapassado
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getImageHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
        }
        float velocidade = 300; // Velocidade de movimentacao do tiro
        // Percorre todos os tiros
        for (Image tiro : tiros){
            // Movimenta o tiro em direção ao topo.
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x,y);
            // Remove os tiros que sairam da tela.
            if (tiro.getY() >= camera.viewportHeight){
                tiros.removeValue(tiro, true); // Remove da lista
                tiro.remove(); // Remove do palco
            }
        }

    }

    /**
     * atualiza a posição do jogador
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 800; // velocidade do movimento do jogador - E verifica se esta dentro da tela.
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
        atirando = false;

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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            atirando = true;
        }
        /*
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            atirando = true;
        }*/
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
        texturaTiro.dispose();
    }
}
