package com.mygdx.projects.testAI.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorGrad {

    private static final Color COLDEST = new Color(0, 0.f, 0.9f, 1);
    private static final Color COLDER = new Color(0, 0.8f, 0.8f, 1);
    private static final Color COLD = new Color(0, 229 / 255f, 133 / 255f, 1);
    private static final Color WARM = new Color(1, 1, 100 / 255f, 1);
    private static final Color WARMER = new Color(1f, 100f / 255f, 0f, 1f);
    private static final Color WARMEST = new Color(1, 0, 0, 1);

    //static Color[] colors = new Color[]{Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};
    static Color[] colors = new Color[]{COLDEST, COLDER, COLD, WARM, WARMER, WARMEST};


    public static Color get(float max, float min, float h, Color out) {
        h = Math.min(Math.max(min, h), max);
        h -= min;
        max -= min;
        float l = max / colors.length;
        int num = (int) (h / l);
        if (num >= colors.length - 1)
            return out.set(colors[colors.length - 1]);
        h = h / l - num;

        out.set(colors[num]).lerp(colors[num + 1], h);
        return out;
    }


}
