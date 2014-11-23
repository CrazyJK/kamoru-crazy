package jk.kamoru.crazy.shop.purifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.domain.Action;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistoryPurifier {

	private File historyFile;
	
	public void setFile(File historyFile) {
		this.historyFile = historyFile;
	}
	
	private List<History> purify() {
		List<String> lines;
		try {
			lines = FileUtils.readLines(historyFile, CRAZY.FILE_ENCODING);
		} catch (IOException e) {
			throw new CrazyException("History file read error", e);
		}
		log.debug("history line {}", lines.size());

		List<History> historyList = new ArrayList<History>();
		for (String line : lines) {
			if (line.trim().length() > 0) {
				String[] parts = StringUtils.split(line, ",", 4);
				try {
					History history = new History();
					if (parts.length > 0)
						history.setDate(new SimpleDateFormat(CRAZY.VIDEO_DATE_PATTERN).parse(parts[0].trim()));
					if (parts.length > 1)
						history.setOpus(parts[1].trim());
					if (parts.length > 2)
						history.setAction(Action.valueOf(parts[2].toUpperCase().trim()));
					if (parts.length > 3)
						history.setDesc(parts[3].trim());
					historyList.add(history);
				}
				catch (Exception e) {
					log.warn("{} - {}", e.getMessage(), line);
				}
			}
		}
		log.debug("historyList.size = {}", historyList.size());
		return historyList;
	}

	public List<History> getList() {
		return purify();
	}
}
