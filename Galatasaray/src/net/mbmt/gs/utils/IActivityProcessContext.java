package net.mbmt.gs.utils;


public interface IActivityProcessContext {
	void process() throws GSException;
	void success();
	void fail(Exception failEx);
}
