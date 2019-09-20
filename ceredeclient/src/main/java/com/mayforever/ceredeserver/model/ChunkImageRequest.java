/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeserver.model;

import com.mayforever.tools.BitConverter;
import java.nio.ByteOrder;

/**
 *
 * @author mis
 */
public class ChunkImageRequest extends BaseClass{

    @Override
    public byte[] toBytes() {
        this.hashSize = hash.length();
        this.requestorHashSize = requestorHash.length();
        this.setTotalSize(hashSize+requestorHashSize+1+4+4+4+4);
        byte[] data = new byte[this.getTotalSize()];
        int index = 0;
        data[index] = this.getProtocol();
        index++;
        System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(BitConverter.intToBytes(hashSize, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(hash.getBytes(), 0, data, index, hash.length());
        index+=hash.length();
        System.arraycopy(BitConverter.intToBytes(requestorHashSize, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(requestorHash.getBytes(), 0, data, index, requestorHash.length());
        index+=requestorHash.length();
        System.arraycopy(BitConverter.intToBytes(chunkNumber, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
//            data[index] = this.getControl();
//            index++;
        return data;
    }

    @Override
    public void fromBytes(byte[] data) {
        int index = 0;
        this.setProtocol(data[index]);
        index++;
        this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;
        this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setHash(new java.lang.String(data, index, hashSize));
        index+=hashSize;
//		this.sethashSize(data.length-9);
        this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
        index+=requestorHashSize;
        this.chunkNumber = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
    }


    public String getRequestorHash() {
        return requestorHash;
    }

    public void setRequestorHash(String requestorHash) {
        this.requestorHash = requestorHash;
    }

    public int getRequestorHashSize() {
        return requestorHashSize;
    }

    public void setRequestorHashSize(int requestorHashSize) {
        this.requestorHashSize = requestorHashSize;
    }

//    public int getBufferSize() {
//        return bufferSize;
//    }
//
//    public void setBufferSize(int bufferSize) {
//        this.bufferSize = bufferSize;
//    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getHashSize() {
        return hashSize;
    }

    public void setHashSize(int hashSize) {
        this.hashSize = hashSize;
    }
//    private byte[] bufferImage = null;
    private String requestorHash = "";
    private int requestorHashSize = 0;
    private int chunkNumber = 0;

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }
    private byte result = 0;

    private String hash = "";
    private int hashSize = 0;
}
