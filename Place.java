package com.example.slidepuzzle;

public class Place {
    private final int x;
    private final int y;
    private final Board board;
    private Tile tile;

    public Place(int x, int y, Board board) {
        this.x = x;
        this.y = y;
        this.board = board;
    }

    public Place(int x, int y, int number, Board board) {
        this(x, y, board);
        tile = new Tile(number);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTile() {
        return tile != null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public boolean slidAble() {
    	return hasTile() && board.slideAble(this);
    }

    public void slide() {
    	board.slide(getTile());
    }
}
