package com.gd.app.collect_core;

import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.geo.util.CoordUtils;

import com.autonavi.collect.constant.CommonConstant;


public class Test {
	
	public static class A{
		private String imageId="a";
		private String path="b";
		private String patha="c";
		private String pathb="c";
		private String pathc="c";
		private String pathd="c";
		private String pathe="c";
		private String pathf="c";
		private String pathg="c";
		private String pathh="c";
		private String pathi="c";
		private String pathj="c";
		private String pathk="c";
		private String pathl="c";
		private String pathm="c";
		private String pathn="c";
		private String patho="c";
		private String pathp="c";
		private String pathq="c";
		private String pathr="c";
		private String paths="c";
		private String patht="c";
		
		

		public String getPatha() {
			return patha;
		}

		public void setPatha(String patha) {
			this.patha = patha;
		}

		public String getPathb() {
			return pathb;
		}

		public void setPathb(String pathb) {
			this.pathb = pathb;
		}

		public String getPathc() {
			return pathc;
		}

		public void setPathc(String pathc) {
			this.pathc = pathc;
		}

		public String getPathd() {
			return pathd;
		}

		public void setPathd(String pathd) {
			this.pathd = pathd;
		}

		public String getPathe() {
			return pathe;
		}

		public void setPathe(String pathe) {
			this.pathe = pathe;
		}

		public String getPathf() {
			return pathf;
		}

		public void setPathf(String pathf) {
			this.pathf = pathf;
		}

		public String getPathg() {
			return pathg;
		}

		public void setPathg(String pathg) {
			this.pathg = pathg;
		}

		public String getPathh() {
			return pathh;
		}

		public void setPathh(String pathh) {
			this.pathh = pathh;
		}

		public String getPathi() {
			return pathi;
		}

		public void setPathi(String pathi) {
			this.pathi = pathi;
		}

		public String getPathj() {
			return pathj;
		}

		public void setPathj(String pathj) {
			this.pathj = pathj;
		}

		public String getPathk() {
			return pathk;
		}

		public void setPathk(String pathk) {
			this.pathk = pathk;
		}

		public String getPathl() {
			return pathl;
		}

		public void setPathl(String pathl) {
			this.pathl = pathl;
		}

		public String getPathm() {
			return pathm;
		}

		public void setPathm(String pathm) {
			this.pathm = pathm;
		}

		public String getPathn() {
			return pathn;
		}

		public void setPathn(String pathn) {
			this.pathn = pathn;
		}

		public String getPatho() {
			return patho;
		}

		public void setPatho(String patho) {
			this.patho = patho;
		}

		public String getPathp() {
			return pathp;
		}

		public void setPathp(String pathp) {
			this.pathp = pathp;
		}

		public String getPathq() {
			return pathq;
		}

		public void setPathq(String pathq) {
			this.pathq = pathq;
		}

		public String getPathr() {
			return pathr;
		}

		public void setPathr(String pathr) {
			this.pathr = pathr;
		}

		public String getPaths() {
			return paths;
		}

		public void setPaths(String paths) {
			this.paths = paths;
		}

		public String getPatht() {
			return patht;
		}

		public void setPatht(String patht) {
			this.patht = patht;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getImageId() {
			return imageId;
		}

		public void setImageId(String imageId) {
			this.imageId = imageId;
		}
		
	}
	
	
	public static void main(String[] s) throws Exception{
		double[] xy=new double[2];
		CoordUtils.gcj2bd(116.3262526, 39.9929450, xy);
		System.out.println(xy);
		
	}
	

}
