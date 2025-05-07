package com.dspread.pos.posAPI;

import android.content.Context;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.common.enums.TransCardMode;
import com.dspread.pos.utils.TRACE;
import com.dspread.xpos.QPOSService;

import java.util.Hashtable;

import me.goldze.mvvmhabit.utils.SPUtils;

public class POSCommand {
    public QPOSService pos;
    public static POSCommand posCommand;
    public static POSCommand getInstance(){
        if(posCommand == null){
            synchronized (POSCommand.class) {
                if (posCommand == null) {
                    posCommand = new POSCommand();
                }
            }
        }
        return posCommand;
    }

    public void setQPOSService(QPOSService pos){
        this.pos = pos;
    }

    public void setDeviceAddress(String address){
        pos.setDeviceAddress(address);
    }

    public void openUart(){
        pos.openUart();
    }

    public void scanQPos2Mode(Context context, long time){
        pos.scanQPos2Mode(context,time);
    }
    public void stopScanQPos2Mode(){
        pos.stopScanQPos2Mode();
    }

    public void stopScanQposBLE(){
        pos.stopScanQposBLE();
    }

    public void connectBLE(String deviceAddress){
        pos.connectBLE(deviceAddress);
    }

    public void connectBluetoothDevice(boolean isAutoBind, int time, String deviceAddress){
        pos.connectBluetoothDevice(isAutoBind,time,deviceAddress);
    }
    public void setCardTradeMode(){
        String modeName = SPUtils.getInstance().getString("cardMode");
        QPOSService.CardTradeMode mode = TransCardMode.valueOf(modeName).getCardTradeModeValue();
        TRACE.i("card mode = "+mode);
        pos.setCardTradeMode(mode);
    }
    public void doTrade(int timeout){
        pos.doTrade(timeout);
    }
    
    public void setAmount(String amount, String cashbackAmounts, String currencyCode, QPOSService.TransactionType transactionType){
        pos.setAmount(amount, cashbackAmounts, currencyCode, transactionType);
    }
    
    public void sendTime(String terminalTime){
        pos.sendTime(terminalTime);
    }
    
    public void selectEmvApp(int position){
        pos.selectEmvApp(position);
    }
    
    public void cancelSelectEmvApp(){
        pos.cancelSelectEmvApp();
    }
    
    public void pinMapSync(String value, int timeout){
        pos.pinMapSync(value, timeout);
    }
    
    public void cancelPin(){
        pos.cancelPin();
    }

    public boolean isOnlinePin(){
        return pos.isOnlinePin();
    }

    public int getCvmPinTryLimit(){
        return pos.getCvmPinTryLimit();
    }
    
    public void bypassPin(){
        pos.sendPin("".getBytes());
    }
    
    public void sendCvmPin(String pinBlock, boolean isEncrypted){
        pos.sendCvmPin(pinBlock, isEncrypted);
    }

    public Hashtable<String,String> getEncryptData(){
        return pos.getEncryptData();
    }

    public Hashtable<String, String> getNFCBatchData(){
        return pos.getNFCBatchData();
    }

    public void doEmvApp(QPOSService.EmvOption option){
        pos.doEmvApp(option);
    }
    
    public void sendOnlineProcessResult(String tlv){
        pos.sendOnlineProcessResult(tlv);
    }
    
    public void cancelTrade(){
        pos.cancelTrade();
    }

    public Hashtable<String, String> anlysEmvIccData(String tlv){
        return pos.anlysEmvIccData(tlv);
    }

    public void close(POS_TYPE posType) {
        TRACE.d("start close");
        if (pos == null || posType == null) {
            TRACE.d("return close");
        } else if (posType == POS_TYPE.BLUETOOTH) {
            pos.disconnectBT();
        } else if (posType == POS_TYPE.BLUETOOTH_BLE) {
            pos.disconnectBLE();
        } else if (posType == POS_TYPE.UART) {
            pos.closeUart();
        } else if (posType == POS_TYPE.USB) {
            pos.closeUsb();
        }
    }
}
