package org.xserver.interfaces.inner;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.stereotype.Component;
import org.xserver.common.util.DateUtil;
import org.xserver.component.annotation.Config;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.jdbc.JdbcTemplate;
import org.xserver.component.json.JsonManager;
import org.xserver.component.json.util.JsonUtil;
import org.xserver.component.listener.XServerListener;
import org.xserver.wrap.HttpInterface;

/**
 * As XServer component, the class support for some inner http interfaces, like
 * memory monitor etc.
 * 
 * @author postonzhang
 * 
 */
@Component
public class InnerInterfaces implements HttpInterface {
	@Resource(name = "jdbcTemplateXServer")
	private JdbcTemplate jdbcTemplateXServer;
	@Config
	private String uploadPath = "/usr/local/dataplatform/xserver/tmp";
	private static final String XSERVER_SHELL = InnerInterfaces.class
			.getClassLoader().getResource("").getPath()
			+ "../sh/xserver.sh";

	public Object memoryMonitorChart(XServerHttpRequest request,
			XServerHttpResponse response) throws Exception {
		int page = request.getParameterInt("page", 1);
		int offset = (page - 1) * 50 + 1;

		String defaultEndDate = DateUtil.tomorrow(DateUtil.DF_yyyy_MM_dd);
		String endDateStr = request.getParameter("endDate", defaultEndDate);
		String defaultBeginDate = DateUtil.add(endDateStr,
				DateUtil.DF_yyyy_MM_dd, Calendar.DAY_OF_MONTH, -7);
		String beginDateStr = request.getParameter("beginDate",
				defaultBeginDate);

		String selectSQL = "SELECT * FROM xserver WHERE time>= ? AND time <= ? ORDER BY seqid DESC LIMIT 50 OFFSET ?";
		List<XServerMem> xserverMems = jdbcTemplateXServer.queryForList(
				selectSQL, XServerMem.class, beginDateStr, endDateStr, offset);
		Map<String, Object> result = JsonUtil.convertChart(xserverMems);

		return JsonManager.mapper(new Object[] { JsonUtil.TITLE, "XServer实时监控",
				JsonUtil.CATEGORIES, result.get(JsonUtil.CATEGORIES),
				JsonUtil.SERIES, result.get(JsonUtil.SERIES) });
	}

	public Object memoryMonitor(XServerHttpRequest request,
			XServerHttpResponse response) throws Exception {
		int page = request.getParameterInt("page", 1);
		int offset = (page - 1) * 50 + 1;

		String defaultEndDate = DateUtil.tomorrow(DateUtil.DF_yyyy_MM_dd);
		String endDateStr = request.getParameter("endDate", defaultEndDate);
		String defaultBeginDate = DateUtil.add(endDateStr,
				DateUtil.DF_yyyy_MM_dd, Calendar.DAY_OF_MONTH, -7);
		String beginDateStr = request.getParameter("beginDate",
				defaultBeginDate);

		String selectSQL = "SELECT * FROM xserver WHERE time >= ? AND time <= ? ORDER BY seqid DESC LIMIT 50 OFFSET ?";
		String countSQL = "SELECT COUNT(*) FROM xserver WHERE time >= ? AND time <= ?";
		List<XServerMem> xserverMems = jdbcTemplateXServer.queryForList(
				selectSQL, XServerMem.class, beginDateStr, endDateStr, offset);
		int count = jdbcTemplateXServer.queryForInt(countSQL, beginDateStr,
				endDateStr);

		return JsonManager.mapper(new Object[] { JsonUtil.MESSAGE,
				count == 0 ? -1 : 0, JsonUtil.HEAD,
				JsonUtil.getBeanDescription(new XServerMem()), JsonUtil.LIST,
				xserverMems, JsonUtil.PAGE, page, JsonUtil.COUNT,
				xserverMems.size(), JsonUtil.BEGIN_DATE, beginDateStr,
				JsonUtil.END_DATE, endDateStr });
	}

	public Object upload(XServerHttpRequest request,
			XServerHttpResponse response) throws IOException {
		List<FileUpload> fileUploads = request.getFileUpload();
		if (fileUploads.size() != 0) {
			FileUpload fileUpload = fileUploads.get(0);
			String fileName = fileUpload.getFilename();
			String filePath = request.getParameterByPost("path", uploadPath);
			File dest = new File(filePath + "/" + fileName);
			fileUpload.renameTo(dest);
			return "{rtn:0}";
		}
		return "{rtn:-1}";
	}

	public Object restart(XServerHttpRequest request,
			XServerHttpResponse response) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		String[] cmdArray = new String[] { XSERVER_SHELL, "restart" };
		runtime.exec(cmdArray);
		return "{rtn:0}";
	}
}
