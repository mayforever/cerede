/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.model;

import java.util.ArrayList;

/**
 *
 * @author mis
 */
public interface Chunk {
          
        public ArrayList<byte[]> toChunkBytes(byte[] data, int chunkCount);
        public void fromChunkBytes(byte[] data, ArrayList<byte[]> dataChunks);
}
