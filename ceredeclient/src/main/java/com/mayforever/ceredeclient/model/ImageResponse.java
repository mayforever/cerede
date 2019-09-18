package com.mayforever.ceredeclient.model;

import java.nio.ByteOrder;
import java.util.Arrays;

import com.mayforever.tools.BitConverter;

public class ImageResponse extends BaseClass{
//	private byte[] bufferImage = null;
	private String requestorHash = "";
	private int requestorHashSize = 0;
//	private int totalSize = 0;
//	private int bufferSize = 0;
	private byte result = 0;
        private int chunkCount = 0;

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }
	public int getHashSize() {
		return hashSize;
	}

	public void setHashSize(int hashSize) {
		this.hashSize = hashSize;
	}
	private int hashSize = 0;
	
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
	private int width = 0;
        private int height = 0;
//	public byte[] getBufferImage() {
//		return bufferImage;
//	}
//
//	public void setBufferImage(byte[] bufferImage) {
//		this.bufferImage = bufferImage;
//	}

	public byte[] toBytes() {
		// TODO Auto-generated method stub
//            this.setBufferSize(bufferImage.length);
            this.setRequestorHashSize(requestorHash.length());
            this.setHashSize(hash.length());
            this.setTotalSize(1+4+4+4+requestorHashSize+4+1+4+hashSize+4);
            byte[] data = new byte[this.getTotalSize()];
            int index = 0;

            data[index] = (byte)3;
            index++;
            System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
                            0, data, index, 4);
            index+=4;
            System.arraycopy(BitConverter.intToBytes(this.getWidth(), ByteOrder.BIG_ENDIAN),
                            0, data, index, 4);
            index+=4;
            System.arraycopy(BitConverter.intToBytes(this.getHeight(), ByteOrder.BIG_ENDIAN),
                            0, data, index, 4);
            index+=4;

//            System.arraycopy(BitConverter.intToBytes(bufferSize, ByteOrder.BIG_ENDIAN),
//                            0, data, index, 4);
//            index+=4;
//            System.arraycopy(bufferImage, 0, data, index,bufferSize);
//            index+=bufferSize;
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
            System.arraycopy(BitConverter.intToBytes(this.getChunkCount(), ByteOrder.BIG_ENDIAN),
                            0, data, index, 4);
            index+=4;
            data[index] = this.getResult();
            index++;
            return data;
	}
	// private int size = 0;
	private String hash = "";
	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		
            int index = 0;
            this.setProtocol(data[index]);
            index++;

            this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
            index+=4;
            this.setWidth(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
            index+=4;
            this.setHeight(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
            index+=4;
//            this.bufferSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
//            index+=4;
//            bufferImage  = new byte[bufferSize];
//            System.arraycopy(data, index, bufferImage, 0, bufferSize);
//            index+=bufferSize;
            this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
            index+=4;
            this.setHash(new java.lang.String(data, index, hashSize));
            index+=hashSize;
        
            this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
            index+=4;
            this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
            index+=requestorHashSize;
            this.chunkCount = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
            index+=4;
            this.setResult(data[index]);
            index++;
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

//	public int getTotalSize() {
//		return totalSize;
//	}
//
//	public void setTotalSize(int totalSize) {
//		this.totalSize = totalSize;
//	}

//	public int getBufferSize() {
//		return bufferSize;
//	}
//
//	public void setBufferSize(int bufferSize) {
//		this.bufferSize = bufferSize;
//	}

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


	
}
