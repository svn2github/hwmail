package com.hs.mail.imap;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;

public class DebugChannel implements Channel {

	@Override
	public int compareTo(Channel arg0) {
		return 0;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public ChannelFactory getFactory() {
		return null;
	}

	@Override
	public Channel getParent() {
		return null;
	}

	@Override
	public ChannelConfig getConfig() {
		return null;
	}

	@Override
	public ChannelPipeline getPipeline() {
		return null;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public boolean isBound() {
		return false;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public SocketAddress getLocalAddress() {
		return null;
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return null;
	}

	@Override
	public ChannelFuture write(Object message) {
		System.out.print(message);
		return null;
	}

	@Override
	public ChannelFuture write(Object message, SocketAddress remoteAddress) {
		return null;
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress) {
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress) {
		return null;
	}

	@Override
	public ChannelFuture disconnect() {
		return null;
	}

	@Override
	public ChannelFuture unbind() {
		return null;
	}

	@Override
	public ChannelFuture close() {
		return null;
	}

	@Override
	public ChannelFuture getCloseFuture() {
		return null;
	}

	@Override
	public int getInterestOps() {
		return 0;
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public ChannelFuture setInterestOps(int interestOps) {
		return null;
	}

	@Override
	public ChannelFuture setReadable(boolean readable) {
		return null;
	}

}
