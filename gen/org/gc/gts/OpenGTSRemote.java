/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\User\\aGTS\\src\\org\\gc\\gts\\OpenGTSRemote.aidl
 */
package org.gc.gts;
public interface OpenGTSRemote extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.gc.gts.OpenGTSRemote
{
private static final java.lang.String DESCRIPTOR = "org.gc.gts.OpenGTSRemote";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.gc.gts.OpenGTSRemote interface,
 * generating a proxy if needed.
 */
public static org.gc.gts.OpenGTSRemote asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.gc.gts.OpenGTSRemote))) {
return ((org.gc.gts.OpenGTSRemote)iin);
}
return new org.gc.gts.OpenGTSRemote.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_loggingState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.loggingState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startLogging:
{
data.enforceInterface(DESCRIPTOR);
this.startLogging();
reply.writeNoException();
return true;
}
case TRANSACTION_pauseLogging:
{
data.enforceInterface(DESCRIPTOR);
this.pauseLogging();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeLogging:
{
data.enforceInterface(DESCRIPTOR);
this.resumeLogging();
reply.writeNoException();
return true;
}
case TRANSACTION_stopLogging:
{
data.enforceInterface(DESCRIPTOR);
this.stopLogging();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.gc.gts.OpenGTSRemote
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int loggingState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_loggingState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startLogging() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void pauseLogging() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resumeLogging() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopLogging() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_loggingState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_pauseLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_resumeLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_stopLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int loggingState() throws android.os.RemoteException;
public void startLogging() throws android.os.RemoteException;
public void pauseLogging() throws android.os.RemoteException;
public void resumeLogging() throws android.os.RemoteException;
public void stopLogging() throws android.os.RemoteException;
}
