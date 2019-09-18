/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.model;

import com.mayforever.tools.BitConverter;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 *
 * @author mis
 */
public abstract class ChunkClass extends BaseClass
//    implements Chunk
{

//    public byte getProtocol() {
//        return protocol;
//    }
//
//    public void setProtocol(byte protocol) {
//        this.protocol = protocol;
//    }

    public int getNumberOfChunk() {
        return numberOfChunk;
    }

    public void setNumberOfChunk(int numberOfChunk) {
        this.numberOfChunk = numberOfChunk;
    }

    public int getTotalChunk() {
        return totalChunk;
    }

    public void setTotalChunk(int totalChunk) {
        this.totalChunk = totalChunk;
    }

    public int getDataPerChunk() {
        return dataPerChunk;
    }

    public void setDataPerChunk(int dataPerChunk) {
        this.dataPerChunk = dataPerChunk;
    }
    
//    private byte protocol = 0;
    private int numberOfChunk = 0;
    private int totalChunk  = 0;
    private int dataPerChunk = 0;
    private int chunkKeySize = 0;

    public int getChunkKeySize() {
        return chunkKeySize;
    }

    public void setChunkKeySize(int chunkKeySize) {
        this.chunkKeySize = chunkKeySize;
    }

    public String getChunkKey() {
        return chunkKey;
    }

    public void setChunkKey(String chunkKey) {
        this.chunkKey = chunkKey;
    }
    private String chunkKey = null;

//    @Override
//    public byte[] toBytes() {
//        return null;
//    }
//
//    @Override
//    public void fromBytes(byte[] data) {
//        
//    }
    
}
