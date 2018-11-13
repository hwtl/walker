package walker.bridge.dubbo;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import walker.common.WalkerConst;
import walker.common.context.WalkerContext;
import walker.common.context.WalkerContextlManager;


@Activate(
        group = {"consumer"},
        order = -10001
)
public class WalkerDubboConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext != null) {
            WalkerContext walkerContext = WalkerContextlManager.initIfContextNull();
            if (walkerContext != null) {
                rpcContext.setAttachment(WalkerConst.WALKER_MASTER_GID, walkerContext.getMasterGid());
            }
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            RpcContext.getContext().clearAttachments();
        }

    }

}
