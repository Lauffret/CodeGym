package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField, countFlags, score;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }
    
    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.RED,"GAME OVER",Color.BLACK,50);
    }
    
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE,"YOU WIN",Color.BLACK,50);
    }
    
    private void countMineNeighbors(){
        List<GameObject> listNeighbors = new ArrayList<>();
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine) {
                    listNeighbors = getNeighbors(gameField[y][x]);
                    for (GameObject neighbors : listNeighbors)
                        if (neighbors.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private void openTile(int x, int y){
        if(!isGameStopped && !gameField[y][x].isOpen && !gameField[y][x].isFlag){
            gameField[y][x].isOpen = true;
            countClosedTiles --;
            if(gameField[y][x].isMine){
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }else{
                if(gameField[y][x].countMineNeighbors >= 1){
                    setCellNumber(x,y,gameField[y][x].countMineNeighbors);
                    setCellColor(x, y, Color.GREEN);
                }else{
                    getNeighbors(gameField[y][x]);
                    List<GameObject> list = getNeighbors(gameField[y][x]);
                    for (GameObject each : list){
                            if (!each.isOpen){
                                openTile(each.x, each.y);
                            }
                    }
                     setCellValue(x,y,"");
                     setCellColor(x, y, Color.GREEN);
                }
                score += 5;
                setScore(score);
                if(countClosedTiles == countMinesOnField){
                win();
                }
            }
            
        }
    }
    
    private void markTile(int x, int y){
        if (!gameField[y][x].isOpen) {
            if (gameField[y][x].isFlag) {
                gameField[y][x].isFlag = false;
                countFlags++;
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            } else if (countFlags != 0) {
                gameField[y][x].isFlag = true;
                countFlags--;
                setCellColor(x, y, Color.YELLOW);
                setCellValue(x, y, FLAG);
                }
            }
    }
    
    @Override
    public void onMouseLeftClick(int x, int y) {
       if (!isGameStopped)
        openTile(x, y);
        else restart();
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
    
    private void restart(){
        isGameStopped = false;
        score = 0;
        setScore(0);
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        createGame();
    }
}
