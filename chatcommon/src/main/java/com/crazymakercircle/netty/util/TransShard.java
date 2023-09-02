package com.crazymakercircle.netty.util;

import com.alibaba.fastjson.JSONObject;
import com.crazymakercircle.util.JsonUtil;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.PARTIAL_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 传输分片 POJO 类
 */
@Slf4j
@Data
public class TransShard {
    /**
     * 匹配传输范围的正则表达式
     */
    private static final Pattern SHARD_RANGE_PATTERN = Pattern.compile("^bytes=(\\d+)-(\\d*)$");

    /**
     * 单个HTTP CHUNK报文的大小
     */
    public static final int HTTPS_CHUNK_SIZE = 8192;
    /**
     * 分片的起始地址、结束地址、文件的总长度
     */
    long start, end, fileLength;

    public TransShard(long fileLength) {
        this.fileLength = fileLength;
    }

    /**
     * 通过请求的range头部参数，计算分片
     *
     * @param ctx     上下文
     * @param request 请求
     * @return 响应报文
     */
    public DefaultHttpResponse compute(ChannelHandlerContext ctx, final HttpRequest request) {
        String range = request.headers()
                              .get(HttpHeaderNames.RANGE);
        // 如果请求不带 range 头部，则返回文件的长度
        if (null == range) {
            log.info(" have no range, returning full content ", range);
            JSONObject object = new JSONObject();
            object.put("fileLength", fileLength);
            HttpProtocolHelper.sendContent(ctx, JsonUtil.pojoToJson(object));
            return null;
        }

        // 如果请求的 range 头部的值与正则表达式不匹配，则返回文件的长度
        Matcher matcher = SHARD_RANGE_PATTERN.matcher(range);
        if (!matcher.matches()) {
            log.info("range '{}' have no  byte-range, returning full content ", range);
            JsonObject object = new JsonObject();
            object.addProperty("fileLength", fileLength);
            HttpProtocolHelper.sendContent(ctx, JsonUtil.pojoToJson(object));
            return null;
        }

        try {
            if (!matcher.group(2)
                        .equals("")) {
                end = Long.parseLong(matcher.group(2));
            }
            // 如果结束地址大于文件长度
            if (end > fileLength) {
                end = fileLength - 1;
            }

            // 解析起始地址
            start = Long.parseLong(matcher.group(1));
            // 如果起始地址不正确
            if (start >= end) {
                log.error("416 Requested Range not satisfiable: start >= end");
                HttpProtocolHelper.sendError(ctx, HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
                return null;
            }

        } catch (Exception ex) {
            log.error("Couldn't parse Range Header", ex);
            HttpProtocolHelper.sendError(ctx, HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            return null;
        }
        DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, PARTIAL_CONTENT);
        /**
         * 设置响应的 content-range  内容区间头部
         */
        response.headers()
                .set(HttpHeaderNames.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
        /**
         * 设置响应的 content-length 头部
         */
        long contentLength = end - start + 1;
        response.headers()
                .set(HttpHeaderNames.CONTENT_LENGTH, contentLength + "");

        return response;
    }

    /**
     * 获取分片的长度
     *
     * @return 分片的长度
     */
    public long getLength() {
        return end - start + 1;
    }
}
