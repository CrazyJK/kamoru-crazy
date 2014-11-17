package jk.kamoru.crazy.shop.purifier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import jk.kamoru.crazy.CRAZY;
import jk.kamoru.crazy.CrazyException;
import jk.kamoru.crazy.domain.Action;
import jk.kamoru.crazy.domain.History;
import jk.kamoru.crazy.service.Purifier;
import jk.kamoru.crazy.service.Warehouse;
import jk.kamoru.crazy.shop.holder.HistoryHolder;
import jk.kamoru.util.FileUtils;
import jk.kamoru.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class HistoryPurifier implements Purifier<File> {

	private List<History> historyList;
	
	private File historyFile;
	
	@Autowired Warehouse warehouse;
	
	@Override
	public void fromHarvester(Collection<File> items) {
		for (File file : items)
			historyFile = file;
		
		purifyAndToWarehouse();
	}

	@Override
	public void purifyAndToWarehouse() {
		List<String> lines;
		try {
			lines = FileUtils.readLines(historyFile, CRAZY.FILE_ENCODING);
		} catch (IOException e) {
			throw new CrazyException("History file read error", e);
		}
		log.debug("history line {}", lines.size());

		for (String line : lines) {
			if (line.trim().length() > 0) {
				String[] parts = StringUtils.split(line, ",", 4);
				History history = new History();
				try {
					if (parts.length > 0)
						history.setDate(new SimpleDateFormat(CRAZY.VIDEO_DATE_PATTERN).parse(parts[0].trim()));
					if (parts.length > 1)
						history.setOpus(parts[1].trim());
					if (parts.length > 2)
						history.setAction(Action.valueOf(parts[2].toUpperCase().trim()));
					if (parts.length > 3)
						history.setDesc(parts[3].trim());
				}
				catch (Exception e) {
					log.warn("{} - {}", e.getMessage(), line);
				}
				historyList.add(history);
			}
		}
		log.debug("historyList.size = {}", historyList.size());
		
		toWarehouse();
	}

	public void toWarehouse() {
		warehouse.fromPurifier(new HistoryHolder(historyList));
	}

}
