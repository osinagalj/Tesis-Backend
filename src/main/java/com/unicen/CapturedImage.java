package com.unicen;

import lombok.Generated;
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
        this.imageValues = new int[height][width];
    }

    public CapturedImage() {

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getChannels() {
        return channels;
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


    // Other methods and implementations...
}