package com.las.cq;

import com.las.cq.robot.CoolQ;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CQGlobal {
    public static Map<Long, CoolQ> robots = new ConcurrentHashMap<>();
}
