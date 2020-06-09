package me.noaz.testplugin.player;

import me.noaz.testplugin.DecryptSha1;

public enum Resourcepack {
    PACK_2D_16X16("https://www.dropbox.com/s/7ay9uuwvu4u0yt1/gunpack2d.zip?dl=1", DecryptSha1.decodeHexString("12c2635e0fcebe62eacf388ddf75c185a59c351c")),
    PACK_3D_DEFAULT("https://www.dropbox.com/s/pk1aerqcf6uyrk2/gunpack3d.zip?dl=1", DecryptSha1.decodeHexString("e2760f1d1e98de26d05a10d305ee1b67e5cc110c"));

    String url;
    byte[] sha1;

    Resourcepack(String url, byte[] sha1) {

        this.url = url;
        this.sha1 = sha1;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getSha1() {
        return sha1;
    }
}
