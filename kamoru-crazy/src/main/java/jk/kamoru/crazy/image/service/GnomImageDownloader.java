package jk.kamoru.crazy.image.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jk.kamoru.tools.web.image.PageImageDownloader;
import jk.kamoru.util.FileUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GnomImageDownloader {
	
	private static final Logger logger = LoggerFactory.getLogger(GnomImageDownloader.class);

	private final String PAGENO_SUFFIX = ".pageno";

	@Value("#{prop['gnom.download']}")			boolean	 download;
	@Value("#{prop['gnom.urlPattern']}")		String   urlPattern;
	@Value("#{prop['gnom.bbsList']}")			String[] bbsList;
	@Value("#{prop['gnom.titleCssQuery']}") 	String   titleCssQuery;
	@Value("#{prop['gnom.downloadPath']}")		String   downloadPath;
	
	@Value("#{prop['gnom.proxy']}")			 	boolean  proxy;
	@Value("#{prop['gnom.proxy.host.name']}") 	String   proxyHostName;
	@Value("#{prop['gnom.proxy.host.value']}") 	String   proxyHostValue;
	@Value("#{prop['gnom.proxy.port.name']}") 	String   proxyPortName;
	@Value("#{prop['gnom.proxy.port.value']}") 	int      proxyPortValue;
	
	@Scheduled(cron="0 0 */1 * * *")
	public void batch() {
		logger.info("batch : gnom image download [{}]", download);
		if (download)
			process();
	}
	
	public void process() {
		logger.info("[그놈 이미지 가져오기 시작]");

		// 저장할 폴더 확인
		File downloadDir = new File(downloadPath, DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		if (!downloadDir.exists())
			try {
				FileUtils.forceMkdir(downloadDir);
			} catch (IOException e) {
				logger.error("저장 폴더 생성 실패", e);
				return;
			}

		// set proxy
		if (proxy) {
			System.setProperty(proxyHostName, proxyHostValue);
			System.setProperty(proxyPortName, String.valueOf(proxyPortValue));
		}
		
		// 게시판별 작업
		for (String bbs : bbsList) { 
			logger.info("  - {} 게시판 시도", bbs);
			// 마지막 페이지 읽어오기
			int pageNo;
			try {
				pageNo = getLastPageNo(bbs);
			} catch (IOException e) {
				logger.error("  - {}의 마지막 페이지를 알수 없음. {}", bbs, e);
				continue;
			}

			// 게시판의 이미지 받기
			// java.util.concurrent.Future 사용 비동기 방식 처리
			int futureCount = 10;
			List<Integer> successList = new ArrayList<Integer>();
			int failCount = 0;
			while (true) {
				// futureCount 만큼 비동기 수행
				ExecutorService eService = Executors.newFixedThreadPool(futureCount);
				List<Future<PageImageDownloader.DownloadResult>> futures = new ArrayList<Future<PageImageDownloader.DownloadResult>>();
				for (int i=0; i<futureCount; i++) {
					pageNo++;
					String pageUrl = String.format(urlPattern, bbs, pageNo, 0, System.currentTimeMillis());
					
					PageImageDownloader pageImageDownloader = 
							new PageImageDownloader(pageUrl, downloadDir.getAbsolutePath(), pageNo, bbs, titleCssQuery);
					pageImageDownloader.setMinimumDownloladSize(FileUtils.ONE_KB * 4);
					/*앞부분에 설정되므로 생략
					if (proxy)
						imageDownloader.setProxyInfo(proxy, proxyHostName, proxyHostValue, proxyPortName, proxyPortValue);
					*/
					futures.add(pageImageDownloader.download(eService));
				}
				eService.shutdown();

				// 결과 확인
				for (Future<PageImageDownloader.DownloadResult> future : futures) {
					try {
						PageImageDownloader.DownloadResult downloadResult = future.get();
						logger.info("  - {} - {}", bbs, downloadResult);
						
						if (downloadResult.result)
							successList.add(downloadResult.no);
						else
							failCount++;
					} catch (InterruptedException e) {
						logger.error("  - {}", e);
						failCount++;
					} catch (ExecutionException e) {
						logger.error("  - {}", e);
						failCount++;
					}
				}
				// 실패 횟수가 누적되면 멈춘다
				if (failCount > futureCount)
					break;
			}
			
			// save page no
			if (successList.size() > 0)
				try {
					pageNo = NumberUtils.max(ArrayUtils.toPrimitive(successList.toArray(new Integer[successList.size()])));
					saveLastPageNo(bbs, pageNo);
					logger.info("  - {} 마지막 페이지 번호 저장 {}", bbs, pageNo);
				} catch (IOException e) {
					logger.error("  - {}의 마지막 페이지 번호를 저장 할 수 없음. pageNo={}", bbs, pageNo);
				}
		}
		
		if (FileUtils.isEmptyDirectory(downloadDir))
			try {
				FileUtils.deleteDirectory(downloadDir);
			} catch (IOException e) {
				logger.error("저장 폴더가 비었으나 삭제에 실패 함", e);
			}
		
		// release proxy
		if (proxy) {
			System.clearProperty(proxyHostName);
			System.clearProperty(proxyPortName);
		}
		
		logger.info("[그놈 이미지 가져오기 완료]");
	}
	
	private void saveLastPageNo(String bbs, int pageNo) throws IOException {
		FileUtils.writeStringToFile(new File(downloadPath, bbs + PAGENO_SUFFIX), String.valueOf(pageNo));
	}

	private int getLastPageNo(String bbs) throws IOException {
		String lastNo = FileUtils.readFileToString(new File(downloadPath, bbs + PAGENO_SUFFIX));
		int pageNo = NumberUtils.toInt(lastNo.trim());
		if (pageNo == 0) 
			throw new IOException("페이지 번호를 알 수 없음. text=" + lastNo);
		return pageNo;
	}

}
