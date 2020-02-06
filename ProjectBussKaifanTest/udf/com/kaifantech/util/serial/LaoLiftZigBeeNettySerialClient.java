package com.kaifantech.util.serial;

import com.kaifantech.bean.iot.client.IotClientBean;
import com.kaifantech.cache.manager.AppCache;
import com.kaifantech.init.sys.params.KfTestCacheKeys;
import com.kaifantech.util.thread.ThreadTool;
import com.calculatedfun.util.msg.AppMsg;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class LaoLiftZigBeeNettySerialClient extends ZigBeeNettyClient {
	public LaoLiftZigBeeNettySerialClient(IotClientBean iotClientBean) {
		super(iotClientBean);
	}

	public AppMsg sendCmd(String innerCmd) {
		if (!getCommObj(getIotClientBean()).isConnected()) {
			return AppMsg.fail();
		}
		ThreadTool.sleep(200);
		AppMsg appMsg = getCommObj(getIotClientBean()).sendCmdLao(innerCmd, getIotClientBean());
		AppCache.worker().hset(KfTestCacheKeys.liftCmd(getIotClientBean().getId()), "" + seq++, appMsg.getMsg());
		return appMsg;
	}

	public void dealData(String msg) {
		if (!(msg.startsWith(getDataHeadStr(getIotClientBean())) || msg.startsWith(getDataHeadStr2(getIotClientBean())))
				|| msg.length() != 14 || !msg.endsWith(ZIGBEE_SEND_DATA_TAIL)) {
			return;
		}
		// System.err.println(DateFactory.getCurrTime(null) + "4-LIFT-CLIENT R:"
		// + msg);
		AppCache.worker().hset(KfTestCacheKeys.liftMsgList(getIotClientBean().getId()), "" + seq++, msg);
		AppCache.worker().hset(KfTestCacheKeys.liftMsgKey(), "" + getIotClientBean().getId(), msg);
	}
}