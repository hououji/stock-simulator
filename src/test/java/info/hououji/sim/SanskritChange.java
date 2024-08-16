package info.hououji.sim;

public class SanskritChange {


	public static char change(char c) {
		String old = "1234567890=#`" ;
		String n = "īāṃṛṇṭḍḷūśṣñḥ" ;
		int idx =old.indexOf(c) ; 
		if(idx != -1) return n.substring(idx, idx+1).charAt(0) ;
		return c; 
	}
	
	public static String change(String s) {
		StringBuffer sb = new StringBuffer() ;
		for(char c: s.toCharArray()) {
			sb.append(change(c)) ;
		}
		return sb.toString() ;
	}
	
	public static void main(String args[]) throws Exception {
//		System.out.println(change("1234567890")) ;
//		System.out.println(change("O3, svabh2va 0uddha, sarva dharma svabh2va 0uddho' ha3."));
//		System.out.println(change("O3, tath2gatod-bhav2ya sv2h2."));
//		System.out.println(change("O3, padmod-bhav2ya sv2h2."));
//		System.out.println(change("O3, vajrod-bhav2ya sv2h2."));
//		System.out.println(change("O3, vajrâgni pra-d1pt2ya sv2h2."));
//		System.out.println(change("O3, k1li k1li vajra vajri-bh9r bandha bandha h93 pha6."));
//		System.out.println(change("O3, s2ra s2ra vajra pr2-k2ra h93 pha6"));
//		System.out.println(change("O3, gagana sa3-bhava vajra ho`."));
//		System.out.println(change("O3, dur dur h9m"));
//		System.out.println(change("Namas try-adhvik2n23 tath2gat2n23. O3, vajrâgni 2-kar=aya sv2h2."));
//		System.out.println(change("O3, 2rolik 2gacchâgaccha sv2h2."));
//		System.out.println(change("O3, am4tod-bhava h93 pha6 sv2h2."));
//		System.out.println(change("O3, vi-sphura5a rak=a vajra-pa#jara   h93 pha6.  "));
//		System.out.println(change("O3, a-samâgni h93 pha6."));
//		System.out.println(change("Nama` samanta buddh2n23. O3, gagana samâsama sv2h2."));
//		System.out.println(change("O3, kamala sv2h2"));
//		System.out.println(change("Nama` sarva tath2gatebhyu vi0va mukhebhya`. Sarvath2 kha3 ud-gata sph2ra hi ma3 gagana ka3 sv2h2."));
//		System.out.println(change("Kamala mukha, kamala locana, kamala 2sana, kamala hasta, kamala bh2man1, kamala kamala, sa3-bhava s2gara, m2l2 k=ara5a, namo stuta"));
//		System.out.println(change("Namo ratna tray2ya. Nama " + 
//				"2ryâvalokite0var2ya bodhisattv2ya mah2 sattv2ya, mah2 k2ru5ik2ya. Tadyath2, o3, cakra vartin cint2-ma5i mah2 padma ru ru sphur ti=6ha jvalâ-kar=aya h93 pha6 sv2h2." + 
//				""));
//		System.out.println(change("O3 padma cint2-ma5i jvala h93."));
//		System.out.println(change("O3, varada padma h93."));
//		System.out.println(change("O3 padma cint2-ma5i jvala h93."));
//		System.out.println(change("Nama` sarva tath2gatebhyu vi0va mukhebhya`. Sarvath2 kha3 ud-gata sph2ra hi ma3 gagana ka3 sv2h2."));
//		System.out.println(change("Kamala mukha, kamala locana, " + 
//				"kamala 2sana, kamala hasta, kamala bh2man1, kamala kamala, sa3-bhava s2gara, m2l2 k=ara5a, namo stuta. " + 
//				""));
//		System.out.println(change("Nama` samanta buddh2n23. O3, gagana samâsama sv2h2"));
//		System.out.println(change("O3, a-samâgni h93 pha6"));
//		System.out.println(change("O3, dur dur h9m."));
//		System.out.println(change("O3, 2rolik gaccha  gaccha sv2h2."));
//		System.out.println(change("O3, huru huru jaya mukhya sv2h2."));
//		System.out.println(change("O3, tath2gatod-bhav2ya sv2h2."));
//		System.out.println(change("O3, padmod-bhav2ya sv2h2. "));
//		System.out.println(change("O3, vajrod-bhav2ya sv2h2."));
//		System.out.println(change("O3, vajrâgni pra-d1pt2ya sv2h2."));
//		
		System.out.println(change("   O3, vairocana m2l2 sv2h2"
				+ ""));
		
		System.out.println(change("O3, vajra guhya j2pa samaye h93"));
		

		
	}
}
