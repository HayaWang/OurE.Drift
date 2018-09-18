package jgibblda;

public class main {

	public static void main(String[] args) {
		testLDA();
//		stopprogram();
//		testLDA_Inferencer();

	}
	
	/**训练LDA
	 * 输入：请见介绍
	 * 输出：LDA训练完成后，其结果会存在于根目录下*/
	private static void testLDA() {

		long beginTime = System.currentTimeMillis();		
		LDAuser ldauser = new LDAuser();
		ldauser.setEst(true);
		//根目录
		ldauser.setDir("C:/Users/Administrator/Desktop/短文本从LDA到SVM/JavaCode/Data");
		//根目录下，需训练的文本数据
		ldauser.setDfile("wiki");
		//每个主题下选择的单词数目
		ldauser.setTwords(20);
		//设置的主题数目，一般设为50或100
		ldauser.setK(100);
		//设置迭代次数，一般设为1000
		ldauser.setNiters(1000);
		ldauser.setSavestep(1000);		
		ldauser.start();

		long endTime = System.currentTimeMillis();
		System.out.println("Running time: "+(endTime-beginTime)+" ms");
		
	}

	/**在原有模型的基础上测试新数据*/
	private static void testLDA_Inferencer() {
		
		LDAuser ldauser = new LDAuser();
		ldauser.setEst(false);
		ldauser.setInf(true);
		ldauser.setDfile("snippet.texts");
		ldauser.setDir("C:/Users/Administrator/Desktop/短文本从LDA到SVM/JavaCode/Data");
		ldauser.setModelName("model-final");
		ldauser.setTwords(20);
		ldauser.setK(50);
		ldauser.setNiters(1000);
		ldauser.setSavestep(1000);
		
		ldauser.start();

		
	}


	/**暂停程序*/
	private static void stopprogram() {
		
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
