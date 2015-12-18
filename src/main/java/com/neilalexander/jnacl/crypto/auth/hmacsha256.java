package com.neilalexander.jnacl.crypto.auth;

import com.neilalexander.jnacl.crypto.verify_32;

import static com.neilalexander.jnacl.crypto.hashblocks.sha256.crypto_hashblocks;

public class hmacsha256 {

  static byte[] iv = new byte[]{
      (byte) 0x6a, (byte) 0x09, (byte) 0xe6, (byte) 0x67,
      (byte) 0xbb, (byte) 0x67, (byte) 0xae, (byte) 0x85,
      (byte) 0x3c, (byte) 0x6e, (byte) 0xf3, (byte) 0x72,
      (byte) 0xa5, (byte) 0x4f, (byte) 0xf5, (byte) 0x3a,
      (byte) 0x51, (byte) 0x0e, (byte) 0x52, (byte) 0x7f,
      (byte) 0x9b, (byte) 0x05, (byte) 0x68, (byte) 0x8c,
      (byte) 0x1f, (byte) 0x83, (byte) 0xd9, (byte) 0xab,
      (byte) 0x5b, (byte) 0xe0, (byte) 0xcd, (byte) 0x19,
  };

  public static int crypto_auth(byte[] out, byte[] in, int inlen, byte[] k) {
    byte[] h = new byte[32];
    byte[] padded = new byte[128];
    int i;
    long bits = 512 + (inlen << 3);

    for (i = 0; i < 32; ++i) h[i] = iv[i];

    for (i = 0; i < 32; ++i) padded[i] = (byte) (k[i] ^ 0x36);
    for (i = 32; i < 64; ++i) padded[i] = 0x36;

    crypto_hashblocks(h, padded, 0, 64);
    int offset = 0;
    crypto_hashblocks(h, in, offset, inlen);
    offset += inlen;
    inlen &= 63;
    offset -= inlen;

    for (i = 0; i < inlen; ++i) padded[i] = in[offset + i];
    padded[inlen] = (byte) 0x80;

    if (inlen < 56) {
      for (i = inlen + 1; i < 56; ++i) padded[i] = 0;
      padded[56] = (byte) ((bits >>> 56) & 0xffL);
      padded[57] = (byte) ((bits >>> 48) & 0xFFL);
      padded[58] = (byte) ((bits >>> 40) & 0xFFL);
      padded[59] = (byte) ((bits >>> 32) & 0xFFL);
      padded[60] = (byte) ((bits >>> 24) & 0xFFL);
      padded[61] = (byte) ((bits >>> 16) & 0xFFL);
      padded[62] = (byte) ((bits >>> 8) & 0xFFL);
      padded[63] = (byte) ((bits) & 0xFFL);
      crypto_hashblocks(h, padded, 0, 64);
    } else {
      for (i = inlen + 1; i < 120; ++i) padded[i] = 0;
      padded[120] = (byte) ((bits >>> 56) & 0xFFL);
      padded[121] = (byte) ((bits >>> 48) & 0xFFL);
      padded[122] = (byte) ((bits >>> 40) & 0xFFL);
      padded[123] = (byte) ((bits >>> 32) & 0xFFL);
      padded[124] = (byte) ((bits >>> 24) & 0xFFL);
      padded[125] = (byte) ((bits >>> 16) & 0xFFL);
      padded[126] = (byte) ((bits >>> 8) & 0xFFL);
      padded[127] = (byte) ((bits) & 0xFFL);
      crypto_hashblocks(h, padded, 0, 128);
    }

    for (i = 0; i < 32; ++i) padded[i] = (byte) (k[i] ^ 0x5c);
    for (i = 32; i < 64; ++i) padded[i] = 0x5c;
    for (i = 0; i < 32; ++i) padded[64 + i] = h[i];

    for (i = 0; i < 32; ++i) out[i] = iv[i];

    for (i = 32; i < 64; ++i) padded[64 + i] = 0;
    padded[64 + 32] = (byte) 0x80;
    padded[64 + 62] = 3;

    crypto_hashblocks(out, padded, 0, 128);

    return 0;
  }

  public static int crypto_auth_verify(byte[] h, byte[] in, int inlen, byte[] k) {
    byte[] correct = new byte[32];
    crypto_auth(correct, in, inlen, k);
    return verify_32.crypto_verify(h, correct);
  }

}
