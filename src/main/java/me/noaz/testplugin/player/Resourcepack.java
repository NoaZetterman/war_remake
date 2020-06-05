package me.noaz.testplugin.player;

public enum Resourcepack {
    PACK_2D_16X16("https://www.dropbox.com/s/7ay9uuwvu4u0yt1/gunpack2d.zip?dl=1"),
    PACK_3D_128X128("https://www.dropbox.com/s/pk1aerqcf6uyrk2/gunpack3d.zip?dl=1");

    String url;

    Resourcepack(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
