package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
    private Stage palcoInf;
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
    private float pontuacao = 0;

    private Array<Texture> texturaExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;




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
        palcoInf = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight,camera));

        initSons();
        initTexturas();
        initFonte();
        initInformacoes();
        initiJogador();
    }

    private void initSons() {
        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");
        initTextExplosao();
    }

    private void initTextExplosao() {
        for (int i = 1;i <= 17; i++){
            texturaExplosao.add(new Texture("sprites/explosion-"+i+".png"));
        }
    }

    /**
     * Instancia os Objetos do Jogador e Adiciona no palco.
      */
    private void initiJogador() {
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

        lblpontuacao = new Label("Pontuação Atual: ", lblEstilo);
        palcoInf.addActor(lblpontuacao);
    }

    /**
     * Instancia os objetos de fonte.
     */
    private void initFonte(){

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;
        param.shadowColor = Color.BLACK;

        fonte = generator.generateFont(param);

        generator.dispose();

//        fonte = new BitmapFont();
    }


    /**
     * Chamado a todo quadro de atualizacao do jogo (FPS)
     * @param delta tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.0f, .0f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lblpontuacao.setPosition(10, camera.viewportHeight - 20);

        if (!gameOver) {
            atualizarTiros(delta);
            capituraTeclas();
            atualizarJogador(delta);
            atualizarMeteoros(delta);
            colidirTiro(delta);
            atualizarExplosoes(delta);
        }
        palco.act(delta);
        palco.draw();
        palcoInf.act(delta);
        palcoInf.draw();
    }

    private boolean gameOver = false;

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes){
            if (explosao.getEstagio() >= 16){
                explosoes.removeValue(explosao, true);
                explosao.getAtor().remove();
            } else {
                explosao.atualizar(delta);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    private float frameExplosao =0 ;
    private void colidirTiro(float delta) {
        for (Image meteoro : meteoro1) {
            for (Image tiro : tiros) {
                if (tiro.getX() >= meteoro.getX() && tiro.getX() <= meteoro.getX() + meteoro.getWidth()) {
                    if (tiro.getY() + tiro.getHeight() >= meteoro.getY()) {
                        meteoro1.removeValue(meteoro, true);
                        meteoro.remove();
                        tiros.removeValue(tiro, true);
                        tiro.remove();
                        pontuacao = pontuacao + meteoro.getY() / 5;
                        lblpontuacao.setText("Pontuação Atual: " + (int) pontuacao);
                        criarExplosao(meteoro.getX(), meteoro.getY());
                    }
                }
            }

        }for (Image meteoro : meteoro2) {
            for (Image tiro : tiros) {
                if (tiro.getX() >= meteoro.getX() && tiro.getX() <= meteoro.getX() + meteoro.getWidth()) {
                    if (tiro.getY() + tiro.getHeight() >= meteoro.getY()) {
                        meteoro2.removeValue(meteoro, true);
                        meteoro.remove();
                        tiros.removeValue(tiro, true);
                        tiro.remove();
                        pontuacao = pontuacao + meteoro.getY() / 5;
                        lblpontuacao.setText("Pontuação Atual: " + (int) pontuacao);
                        criarExplosao(meteoro.getX(), meteoro.getY());
                    }
                }
            }
        }
    }

    private void criarExplosao(float x, float y){
        Image ator = new Image(texturaExplosao.get(0));
        ator.setPosition(x, y);
        palco.addActor(ator);
        somExplosao.play();
        Explosao explosao = new Explosao(ator, texturaExplosao);
        explosoes.add(explosao);
    }

    // ---------------------------------------------------------------------------------------------
    private float intervaloMeteoro = 100;

    private void atualizarMeteoros(float delta) {
        int tipo = MathUtils.random(1,3);
        intervaloMeteoro = intervaloMeteoro + delta * 70;
        if (tipo == 1 && intervaloMeteoro >= 100) {
            // criar meteoro1
            Image meteoro = new Image(texturaMeteoro1);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x,y);
            meteoro1.add(meteoro);
            palco.addActor(meteoro);
            intervaloMeteoro = MathUtils.random(1,100);
        }else if (tipo == 2 && intervaloMeteoro >= 100){
            // criar meteoro2
            Image meteoro = new Image(texturaMeteoro2);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
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
            if ((meteoro.getY()<=jogador.getY()+jogador.getImageHeight())&&(meteoro.getX()>=jogador.getX()-(jogador.getImageWidth()))&&(meteoro.getX()<=jogador.getX()+(jogador.getImageWidth()))){
                gameOver = true;
                somGameOver.play();
            }
            if (meteoro.getY() <= 0){
                meteoro2.removeValue(meteoro, true);
                meteoro.remove();
            }
        }
        for (Image meteoro : meteoro1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - MathUtils.random(velocidade + 10, velocidade - 10) * delta;
            meteoro.setPosition(x,y);
            if ((meteoro.getY()<=jogador.getY()+jogador.getImageHeight())&&(meteoro.getX()>=jogador.getX()-(jogador.getImageWidth()))&&(meteoro.getX()<=jogador.getX()+(jogador.getImageWidth()))){
                gameOver = true;
                somGameOver.play();
            }
            if (meteoro.getY() <= 0){
                meteoro1.removeValue(meteoro, true);
                meteoro.remove();
            }
        }
    }

    private final float MIN_INTERVALO_TIROS = 0.1f; // Minimo tempo entre tiros.
    private float intervaloTiros = 100; // Tempo acumulado entre os tiros

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
                somTiro.play();
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
        }
        */
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
        for (Texture text : texturaExplosao){
            text.dispose();
        }
        palcoInf.dispose();
        somTiro.dispose();
        somExplosao.dispose();
        somGameOver.dispose();
        musicaFundo.dispose();
    }
}
