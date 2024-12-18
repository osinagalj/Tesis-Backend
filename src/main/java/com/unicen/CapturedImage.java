package com.unicen;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CapturedImage {
    private int width;
    private int height;
    private int depth;
    private int channels;
    private int[][] imageValues;

    public CapturedImage(int width, int height, int depth, int channels) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.channels = channels;
        this.imageValues = new int[width][height];
    }

    public CapturedImage(int width, int height, int[][] imageValues) {
        this.width = width;
        this.height = height;
        this.imageValues = imageValues;
    }

    public CapturedImage() {

    }

    public void setValueImage(int row, int col, int value) {
        imageValues[row][col] = value;
    }

    public int getValueImage(int row, int col) {
        return imageValues[row][col];
    }

    public int[][] getImageData() {
        return imageValues;
    }

    public void setImageValue(int[][] image){
        this.imageValues = image;
    }


}