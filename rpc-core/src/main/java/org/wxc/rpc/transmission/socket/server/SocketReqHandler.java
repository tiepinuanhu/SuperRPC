package org.wxc.rpc.transmission.socket.server;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.wxc.rpc.dto.RPCRequest;
import org.wxc.rpc.dto.RPCResponse;
import org.wxc.rpc.handler.RPCReqHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * 服务端线程池执行的操作
 * @author wangxinchao
 * @date 2025/10/14 20:42
 */
@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable{


    private final Socket socket;


    private final RPCReqHandler reqHandler;

    @SneakyThrows
    @Override
    public void run() {
        ObjectInputStream objectInputStream
                = new ObjectInputStream(socket.getInputStream());
        RPCRequest request = (RPCRequest)objectInputStream.readObject();
        System.out.println("request = " + request);


        // 处理请求, 反射调用
        Object data = reqHandler.invoke(request);

        log.debug("调用结果：{}", data);
        // 返回结果
        RPCResponse<Object> success =
                RPCResponse.success(request.getRequestId(),  data);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        objectOutputStream.writeObject(success);

        objectOutputStream.flush();
    }
}
