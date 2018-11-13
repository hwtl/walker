package walker.bridge.dubbo;


import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.apache.commons.lang3.StringUtils;
import walker.common.WalkerConst;
import walker.common.context.WalkerContextlManager;

@Activate(
        group = {"provider"},
        order = -10001
)
public class WalkerDubboProviderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String walkerTccGid = invocation.getAttachment(WalkerConst.WALKER_MASTER_GID);
        if (StringUtils.isNotEmpty(walkerTccGid)) {
            WalkerContextlManager.inherit(walkerTccGid);
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }

}
