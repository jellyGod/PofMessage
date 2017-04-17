package org.openflow.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFCounter;
import org.openflow.protocol.OFCounter.OFCounterModCmd;
import org.openflow.protocol.OFCounterMod;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFFlowMod.OFFlowEntryCmd;
import org.openflow.protocol.OFGroupMod;
import org.openflow.protocol.OFGroupMod.OFGroupModCmd;
import org.openflow.protocol.OFGroupMod.OFGroupType;
import org.openflow.protocol.OFMatch20;
import org.openflow.protocol.OFMatchX;
import org.openflow.protocol.OFMeterMod;
import org.openflow.protocol.OFMeterMod.OFMeterModCmd;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionAddField;
import org.openflow.protocol.action.OFActionCalculateCheckSum;
import org.openflow.protocol.action.OFActionCounter;
import org.openflow.protocol.action.OFActionDeleteField;
import org.openflow.protocol.action.OFActionDrop;
import org.openflow.protocol.action.OFActionExterimenter;
import org.openflow.protocol.action.OFActionGroup;
import org.openflow.protocol.action.OFActionModifyField;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionPacketIn;
import org.openflow.protocol.action.OFActionSetField;
import org.openflow.protocol.action.OFActionSetFieldFromMetadata;
import org.openflow.protocol.factory.OFMessageFactory;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.instruction.OFInstructionCalculateField;
import org.openflow.protocol.instruction.OFInstructionConditionalJmp;
import org.openflow.protocol.instruction.OFInstructionGotoDirectTable;
import org.openflow.protocol.instruction.OFInstructionGotoTable;
import org.openflow.protocol.instruction.OFInstructionMeter;
import org.openflow.protocol.instruction.OFInstructionMovePacketOffset;
import org.openflow.protocol.instruction.OFInstructionWriteMetadata;
import org.openflow.protocol.instruction.OFInstructionWriteMetadataFromPacket;
import org.openflow.protocol.table.OFFlowTable;
import org.openflow.protocol.table.OFTableMod;
import org.openflow.protocol.table.OFTableMod.OFTableModCmd;
import org.openflow.protocol.table.OFTableType;
import org.openflow.util.HexString;

public class MessageTestThread extends Thread{
	private OFMessageAsyncStream stream;
    private OFMessageFactory off;
	public MessageTestThread()
	{
		
	}
	
	public MessageTestThread(OFMessageAsyncStream stream)
	{
		this.stream=stream;
		off=stream.getMessageFactory();
	}
	
	//封装一系列的测试方法
	//方便在其中加以测试
	//默认添加第一张表格
	public void addTable(int tableId,String tableName,List<OFMatch20> list)
	{
		OFTableMod ofm=(OFTableMod) off.getOFMessage(OFType.TABLE_MOD);
		OFFlowTable oft=new OFFlowTable();
		oft.setCommand(OFTableModCmd.OFPTC_ADD);
		oft.setTableType(OFTableType.OF_MM_TABLE);
		oft.setTableId((byte) tableId);
		oft.setTableName(tableName);
		oft.setTableSize(128);
		oft.setMatchFieldNum((byte) 2);
		oft.setKeyLength((short) 64);
		oft.setMatchFieldList(list);
		ofm.setFlowTable(oft);
		try {
			System.out.println(ofm);
			stream.write(ofm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearTable(OFTableMod table)
	{
		table.getFlowTable().setCommand(OFTableModCmd.OFPTC_DELETE);
		try {
			System.out.println(table);
			stream.write(table);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//默认添加的表项
	public void addFlowEntry(int tableId,List<OFInstruction> ilist)
	{
		OFFlowMod ofm=(OFFlowMod) off.getOFMessage(OFType.FLOW_MOD);
		ofm.setCommand((byte) (OFFlowEntryCmd.OFPFC_ADD.ordinal()));
		ofm.setMatchFieldNum((byte) 2);
		ofm.setInstructionNum((byte)ilist.size());
		ofm.setCounterId(3);
		ofm.setCookie(0);
		ofm.setCookieMask(0);
		ofm.setTableId((byte) 0);
		ofm.setTableType(OFTableType.OF_MM_TABLE);
		ofm.setPriority((short) 1);
		//定义match的值
		List<OFMatchX> list=new ArrayList<OFMatchX>();
		OFMatchX ox1=new OFMatchX();
		ox1.setFieldId((short) 1);
		ox1.setOffset((short) 0);
		ox1.setLength((short) 48);
		ox1.setValue(HexString.parseTextToHexBytes("0"));
		ox1.setMask(HexString.parseTextToHexBytes("0"));
		
		OFMatchX ox2=new OFMatchX();
		ox2.setFieldId((short) 3);
		ox2.setOffset((short) 96);
		ox2.setLength((short) 16);
		ox2.setValue(HexString.parseTextToHexBytes("0888"));
		ox2.setMask(HexString.parseTextToHexBytes("ffff"));
		list.add(ox1);
		list.add(ox2);
		ofm.setMatchList(list);
		
		//设置instruction
		ofm.setInstructionList(ilist);
		try {
			System.out.println(ofm);
			stream.write(ofm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		/*
		//发送消息here
		System.out.println("i will send many message!");
		//add table 0
		int tableId1=0;
		String tableName1="First Table";
		List<OFMatch20> list1=new ArrayList<OFMatch20>();
		//第一个match
		OFMatch20 mat1=new OFMatch20();
		mat1.setFieldId((short) 1);
		mat1.setFieldName("Dmac");
		mat1.setOffset((short) 0);
		mat1.setLength((short) 48);
		list1.add(mat1);
		//第二个match
		OFMatch20 mat2=new OFMatch20();
		mat2.setFieldId((short) 3);
		mat2.setFieldName("Eth Type");
		mat2.setOffset((short) 96);
		mat2.setLength((short) 16);
		list1.add(mat2);
		addTable(tableId1,tableName1,list1);
		
		//添加第一个table的FlowEntry
		List<OFInstruction> ilist1=new ArrayList<OFInstruction>();
		//添加第一个instruction
		OFInstructionGotoTable oigt=new OFInstructionGotoTable();
		oigt.setMatchFieldNum((byte) 2);
		oigt.setNextTableId((byte) 1);
		oigt.setMatchList(list1);
		ilist1.add(oigt);
		//添加第二个instruction
		OFInstructionWriteMetadata oiwm=new OFInstructionWriteMetadata();
		oiwm.setMetadataOffset((short) 0);
		oiwm.setWriteLength((short) 10);
		oiwm.setValue(HexString.fromHexString("12"));
		ilist1.add(oiwm);
		//添加第三个instruction
		OFInstructionMeter oim=new OFInstructionMeter();
		oim.setMeterId(0);
		ilist1.add(oim);
		//添加第四个instruction
		OFInstructionWriteMetadataFromPacket oiwmdfp=new OFInstructionWriteMetadataFromPacket();
		oiwmdfp.setMetadataOffset((short) 0);
		oiwmdfp.setPacketOffset((short) 0);
		oiwmdfp.setWriteLength((short) 0);
		ilist1.add(oiwmdfp);
		//添加第五个instruction
		OFInstructionGotoDirectTable oigdt=new OFInstructionGotoDirectTable();
		ilist1.add(oigdt);
		//添加第六个instruction
		OFInstructionConditionalJmp oicjmp=new OFInstructionConditionalJmp();
		ilist1.add(oicjmp);
		addFlowEntry(tableId1,ilist1);
		
		//add table 1
		int tableId2=1;
		String tableName2="Second Table";
		List<OFMatch20> list2=new ArrayList<OFMatch20>();
		list2.add(mat1);
		list2.add(mat2);
		addTable(tableId2,tableName2,list2);
		
		//添加表项，尽量覆盖所有的数据
		List<OFInstruction> ilist2=new ArrayList<OFInstruction>();
		OFInstructionCalculateField oicf=new OFInstructionCalculateField();
		ilist2.add(oicf);
		OFInstructionMovePacketOffset oimpo=new OFInstructionMovePacketOffset();
		ilist2.add(oimpo);
		//applty action 1
		OFInstructionApplyActions oiaa=new OFInstructionApplyActions();
		List<OFAction> alist=new ArrayList<OFAction>();
		alist.add(new OFActionAddField());
		alist.add(new OFActionCalculateCheckSum());
		alist.add(new OFActionCounter());
		alist.add(new OFActionDeleteField());
		alist.add(new OFActionDrop());
		//alist.add(new OFActionExterimenter());
		oiaa.setActionNum((byte) alist.size());
		oiaa.setActionList(alist);
		ilist2.add(oiaa);
		//这样就可以试验很多哦
		//apply action 2
		OFInstructionApplyActions oia2=new OFInstructionApplyActions();
		List<OFAction> alist2=new ArrayList<OFAction>();
		OFActionModifyField oamf=new OFActionModifyField();
		oamf.setMatchField(new OFMatch20());
		alist2.add(oamf);
		alist2.add(new OFActionOutput());
		alist2.add(new OFActionGroup());
		alist2.add(new OFActionPacketIn());
		OFActionSetField ofsf=new OFActionSetField();
		ofsf.setFieldSetting(new OFMatchX());
		alist2.add(ofsf);
		OFActionSetFieldFromMetadata ofsffm=new OFActionSetFieldFromMetadata();
		ofsffm.setFieldSetting(new OFMatch20());
		alist2.add(ofsffm);
		oia2.setActionNum((byte) alist2.size());
		oia2.setActionList(alist2);
		ilist2.add(oia2);
		
		addFlowEntry(tableId2,ilist2);
		//在测试一个Packet_out这样比较好
		OFPacketOut ofo=new OFPacketOut();
		try {
			stream.write(ofo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//测试一下groupMod
		OFGroupMod ogm=new OFGroupMod();
		ogm.setCommand(OFGroupModCmd.OFPGC_ADD);
		ogm.setGroupId(1);
		ogm.setCounterId(1);
		try {
			stream.write(ogm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//测试一下meter mod
		OFMeterMod ofmm=new OFMeterMod();
		ofmm.setCommand(OFMeterModCmd.OFPMC_ADD);
		try {
			stream.write(ofmm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//测试一下counter mod
		OFCounterMod ofmod=new OFCounterMod();
		OFCounter ofc=new OFCounter();
		ofc.setCommand(OFCounterModCmd.OFPCC_ADD);
		ofmod.setCounter(ofc);
		try {
			stream.write(ofmod);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//这个需要在多测试一下，问题总是可以解决的
		//multipart_request
		
	}
}
