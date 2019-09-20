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

public class ChunkImageResponse extends BaseClass{

    @Override
    public byte[] toBytes() {
        this.setBufferSize(bufferImage.length);
        this.setRequestorHashSize(requestorHash.length());
        this.setHashSize(hash.length());
        this.setTotalSize(bufferSize+1+4+4+requestorHashSize+4+4+hashSize+1+4+4);
        byte[] data = new byte[this.getTotalSize()];
        int index = 0;

        data[index] = this.getProtocol();
        index++;
        
        System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;

		
        System.arraycopy(BitConverter.intToBytes(bufferSize, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(bufferImage, 0, data, index,bufferSize);
        index+=bufferSize;
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
        data[index] = this.getResult();
        index++;
        System.arraycopy(BitConverter.intToBytes(chunkNumber, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(BitConverter.intToBytes(totalChunk, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        return data;
    }

    @Override
    public void fromBytes(byte[] data) {
        int index = 0;
        this.setProtocol(data[index]);
        index++;

        this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;
        
        this.bufferSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        bufferImage  = new byte[bufferSize];
        System.arraycopy(data, index, bufferImage, 0, bufferSize);
        index+=bufferSize;
        this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setHash(new java.lang.String(data, index, hashSize));
        index+=hashSize;
        
        this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
        index+=requestorHashSize;
        this.setResult(data[index]);
        index++;
        this.chunkNumber = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.totalChunk = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
    }

    public byte[] getBufferImage() {
        return bufferImage;
    }

    public void setBufferImage(byte[] bufferImage) {
        this.bufferImage = bufferImage;
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

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

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
    private byte[] bufferImage = null;
    private String requestorHash = "";
    private int requestorHashSize = 0;
    private int bufferSize = 0;
    private byte result = 0;

    private String hash = "";
    private int hashSize = 0;
    private int chunkNumber = 0;
    private int totalChunk = 0;

    public int getTotalChunk() {
        return totalChunk;
    }

    public void setTotalChunk(int totalChunk) {
        this.totalChunk = totalChunk;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }
//    @Override
//    public ArrayList<byte[]> toChunkBytes() {
//        
//    }
//
//    @Override
//    public void fromChunkBytes(byte[] data, ArrayList<byte[]> dataChunks) {
//        
//    }

//    @Override
//    public ArrayList<byte[]> toChunkBytes(byte[] data, int chunkCount) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        int dataCount = data.length;
//    	ArrayList<byte[]> chunkdata = new ArrayList<byte[]>(); 
//    	String SUFFIX = ".part";
//    	int lengthPerChunk = Math.floorDiv(dataCount ,chunkCount);
//    	int indexOfData = 0;
//    	for(int i = 1; i < chunkCount ; i++) {
//    		if(chunkCount ==i) {
//    			byte[] dataChunk = new byte[data.length - indexOfData + 5];
//    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
//    			indexOfData+=lengthPerChunk;
//    			System.arraycopy(SUFFIX.getBytes(),0 , dataChunk, lengthPerChunk, 5);
//    			indexOfData+=5;
//    			chunkdata.add(dataChunk);
//    		}else {
//    			byte[] dataChunk = new byte[lengthPerChunk];
//    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
//    			indexOfData+=lengthPerChunk;
//    			System.arraycopy(SUFFIX.getBytes(),0 , dataChunk, lengthPerChunk, 5);
//    			indexOfData+=5;
//    			chunkdata.add(dataChunk);
//    		}
//    		
//    	}
//    	return chunkdata;
//    }
//
//    @Override
//    public void fromChunkBytes(byte[] data, ArrayList<byte[]> dataChunks) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
