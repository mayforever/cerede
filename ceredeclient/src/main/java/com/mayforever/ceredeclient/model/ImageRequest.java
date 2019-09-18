package com.mayforever.ceredeclient.model;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class ImageRequest extends BaseClass{
	public String gethash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
        // set hash
	private String hash = "";
        // set requestorHash 
	private String requestorHash = "";
        
	private int requestorHashSize = 0;
	private int hashSize = 0;
//	private byte control = 0;

    public String getHash() {
        return hash;
    }

    public int getRequestorHashSize() {
        return requestorHashSize;
    }

//    public int getWidth() {
//        return width;
//    }
//
//    public int getHeight() {
//        return height;
//    }
//    private int width = 0;
//    private int height = 0;
//	private int totalSize = 0;
    public byte[] toBytes() {
            // TODO Auto-generated method stub
            this.hashSize = hash.length();
            this.requestorHashSize = requestorHash.length();
            this.setTotalSize(hashSize+requestorHashSize+1+4+4+4);
            byte[] data = new byte[this.getTotalSize()];
            int index = 0;
            data[index] = (byte)2;
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
//            data[index] = this.getControl();
//            index++;
            return data;
    }

    public void fromBytes(byte[] data) {
            // TODO Auto-generated method stub
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
//            this.setControl(data[index]);
//            index++;
    }

    public String getRequestorHash() {
            return requestorHash;
    }

    public void setRequestorHash(String requestor) {
            this.requestorHash = requestor;
    }

    public int getHashSize() {
            return hashSize;
    }

    public void setHashSize(int hashSize) {
            this.hashSize = hashSize;
    }

    /**
     * @return the requestorHashSize 
     */
    public int getRequestorSize() {
            return requestorHashSize;
    }

    /**
     * @param requestorHashSize the requestorHashSize to set
     */
    public void setRequestorSize(int requestorHashSize) {
            this.requestorHashSize = requestorHashSize;
    }

//    public byte getControl() {
//            return control;
//    }
//
//    public void setControl(byte control) {
//            this.control = control;
//    }

//	public int getTotalSize() {
//		return totalSize;
//	}
//
//	public void setTotalSize(int totalSize) {
//		this.totalSize = totalSize;
//	}
}
