package com.zebra.rfidreader.nonghang.zhongyou.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserHandler extends DefaultHandler {

	/**
	 * �洢���еĽ�������
	 */
	private List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();
	 	  
	public XmlParserHandler() {
		
	}

	public List<ProvinceModel> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// ��������һ����ʼ��ǩ��ʱ�򣬻ᴥ���������
	}

	ProvinceModel provinceModel = new ProvinceModel();
	CityModel cityModel = new CityModel();
	DistrictModel districtModel = new DistrictModel();
	
	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		// ��������ʼ��ǵ�ʱ�򣬵����������
		if (qName.equals("province")) {
			provinceModel = new ProvinceModel();
			provinceModel.setProvinceID(attributes.getValue(0));
			provinceModel.setProvince(attributes.getValue(1));
			provinceModel.setCityList(new ArrayList<CityModel>());
		} else if (qName.equals("City")) {
			cityModel = new CityModel();
			cityModel.setCityID(attributes.getValue(0));
			cityModel.setCity(attributes.getValue(1));
			cityModel.setDistrictList(new ArrayList<DistrictModel>());
		} else if (qName.equals("Piecearea")) {
			districtModel = new DistrictModel();
			districtModel.setPieceareaID(attributes.getValue(0));
			districtModel.setPiecearea(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// ����������ǵ�ʱ�򣬻�����������
		if (qName.equals("Piecearea")) {
			cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("City")) {
        	provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
        	provinceList.add(provinceModel);
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
