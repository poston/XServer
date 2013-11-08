package org.xserver.component.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.annotation.Config;
import org.xserver.component.hbase.util.HBaseUtil;

/**
 * Inspired by JdbcTemplate, the class define many common useful method to use
 * HBase. Because we use spring to inversion control, you should configure at
 * <strong><code>hbaseContext.xml</code></strong> (at the project classpath).
 * The argument '<code>hbase.htable.pool.size</code>' mean a pool can be created
 * with a <i>maxSize</i> which defines the most HTable references that will ever
 * be retained for each table, its value can configure at <code>
 * classpath:hbase.properties</code>.
 * 
 * <h3>HBase data structure</h3>
 * <p>
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr>
 * <th>rowKey</th>
 * <th>column family 1</th>
 * <th>column family 2</th>
 * <th>...</th>
 * </tr>
 * <tr>
 * <td></td>
 * <td><strong>qualifier 1_1 | qualifier 1_2 | ...</strong></td>
 * <td><strong>qualifier 2_1 | qualifier 2_2 | ...</strong></td>
 * <td><strong>...</strong></td>
 * </tr>
 * </table>
 * 
 * <h3>Note</h3>
 * 
 * <p>
 * This template operations are compatible in many HBase versions, but HBase's
 * {@link HTablePool} maybe use differently. When occur network or connection
 * problem, first to see <code>HTablePool</code>.
 * <ul>
 * <li>HBase version 0.90.5</li>
 * <p>
 * To use, instantiate an HTablePool and use {@link #getTable(String)} to get an
 * HTable from the pool. Once you are done with it, return it to the pool with
 * {@link #putTable(HTableInterface)}.
 * </p>
 * <li>HBase version 0.92.2</li>
 * <p>
 * Once you are done with it, close your instance of {@link HTableInterface} by
 * calling {@link HTableInterface#close()} rather than returning the tables to
 * the pool with (deprecated) {@link #putTable(HTableInterface)}.
 * </p>
 * 
 * @author postonzhang
 * @since 2013/08/06
 * 
 */
public class HBaseTemplate implements HBaseOperations {
	private HTablePool pool;
	private String tableName;
	private int scanCaching;
	@Config
	private int defaultScanCaching = 1000;

	private static final Logger log = LoggerFactory.getLogger(HBaseUtil.class);

	public HBaseTemplate(HTablePool pool, String tableName, int scanCaching) {
		this.pool = pool;
		this.tableName = tableName;
		this.scanCaching = scanCaching;
	}

	public HBaseTemplate(HTablePool pool, String tableName) {
		this.pool = pool;
		this.tableName = tableName;
		this.scanCaching = defaultScanCaching;
	}

	@Override
	public void put(Put put) throws IOException {
		HTableInterface table = pool.getTable(tableName);
		table.put(put);
		HBaseUtil.putTableOrClose(pool, table);
	}

	public <T> void put(String row, T t) {
		HTableInterface table = pool.getTable(tableName);
		Put put = new Put(Bytes.toBytes(row));
		try {
			HBaseUtil.createPut(put, t);
			table.put(put);
		} catch (Exception e) {
			log.error("put " + t.toString() + " to hbase error", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}
	}

	public void put(String row, String family, String qualifier, String value)
			throws IOException {
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
				Bytes.toBytes(value));
		put(put);
	}

	@Override
	public void putBatch(List<Put> puts) {
		HTableInterface table = pool.getTable(tableName);
		try {
			table.put(puts);
		} catch (Exception e) {
			log.error("put batch execute error in the hbase process", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}
	}

	/**
	 * Put rows into hbase batch, the map argument is
	 * 
	 * @param map
	 */
	public <T> void putBatch(Map<String, T> map) {
		List<Put> puts = new ArrayList<Put>(map.size());
		try {
			for (Entry<String, T> entry : map.entrySet()) {
				Put put = new Put(Bytes.toBytes(entry.getKey()));
				HBaseUtil.createPut(put, entry.getValue());
				puts.add(put);
			}
		} catch (Exception e) {
			log.error("put batch execute error in the create puts process ", e);
		}

		putBatch(puts);
	}

	@Override
	public Result get(Get get) {
		HTableInterface table = pool.getTable(tableName);

		try {
			return table.get(get);
		} catch (Exception e) {
			log.error("get result from hbase error", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}

		return null;
	}

	/**
	 * Get a row from HBase, you should consider using
	 * {@link #getBean(String, Class)} method first.
	 * 
	 * @param row
	 *            the row
	 * @return
	 */
	public Result get(String row) {
		Get get = new Get(Bytes.toBytes(row));
		return get(get);
	}

	@Override
	public Result[] getBatch(List<Get> gets) {
		HTableInterface table = pool.getTable(tableName);

		try {
			return table.get(gets);
		} catch (Exception e) {
			log.error("get batch result from hbase error", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}

		return null;
	}

	public Result[] getBatchByRows(List<String> rows) {
		List<Get> gets = new ArrayList<Get>(rows.size());
		for (String row : rows) {
			gets.add(new Get(Bytes.toBytes(row)));
		}

		return getBatch(gets);
	}

	public <T> List<T> getBatchBean(List<String> rows, Class<T> clazz) {
		List<T> ts = new ArrayList<T>(rows.size());
		try {
			for (Result result : getBatchByRows(rows)) {
				ts.add(HBaseUtil.reflectRow(result.list(), clazz));
			}
		} catch (Exception e) {
			log.error("get bean batch from hbase error", e);
		}

		return ts;
	}

	@Override
	public void delete(Delete delete) throws IOException {
		HTableInterface table = pool.getTable(tableName);
		table.delete(delete);
		HBaseUtil.putTableOrClose(pool, table);
	}

	@Override
	public void deleteBatch(List<Delete> deletes) throws IOException {
		HTableInterface table = pool.getTable(tableName);
		table.delete(deletes);
		HBaseUtil.putTableOrClose(pool, table);
	}

	@Override
	public void batch(List<Row> actions, Object[] results) {
		HTableInterface table = pool.getTable(tableName);
		try {
			table.batch(actions, results);
		} catch (Exception e) {
			log.error("batch operations error", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}
	}

	@Override
	public Object[] batch(List<Row> actions) {
		HTableInterface table = pool.getTable(tableName);

		try {
			return table.batch(actions);
		} catch (Exception e) {
			log.error("batch operations error", e);
		} finally {
			HBaseUtil.putTableOrClose(pool, table);
		}

		return null;
	}

	/**
	 * Return HBase original {@link ResultScanner}, you should consider using
	 * {@link #scanBean(String, String, Class)} method first.
	 * 
	 * @param startRow
	 *            the start row
	 * @param stopRow
	 *            the stop row
	 * @return {@link ResultScanner}
	 */
	public ResultScanner scan(String startRow, String stopRow) {
		HTableInterface table = pool.getTable(tableName);

		Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
		scan.setCaching(scanCaching);
		ResultScanner rs = null;
		try {
			rs = table.getScanner(scan);
		} catch (IOException e) {
			log.error("get scanner execute error", e);
		} finally {
			rs.close();
			HBaseUtil.putTableOrClose(pool, table);
		}

		return rs;
	}

	/**
	 * Return the specified class list
	 * 
	 * @param startRow
	 *            the start row
	 * @param stopRow
	 *            the stop row
	 * @param clazz
	 *            the specified class
	 * @return the list of class
	 */
	public <T> List<T> scanBean(String startRow, String stopRow, Class<T> clazz) {
		HTableInterface table = pool.getTable(tableName);

		Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
		scan.setCaching(scanCaching);
		List<T> ts = new ArrayList<T>();
		ResultScanner rs = null;
		try {
			rs = table.getScanner(scan);

			for (Result result : rs) {
				ts.add(HBaseUtil.reflectRow(result.list(), clazz));
			}
		} catch (Exception e) {
			log.error("scan bean from hbase error", e);
		} finally {
			rs.close();
			HBaseUtil.putTableOrClose(pool, table);
		}

		return ts;
	}

	public List<Map<String, Map<String, Number>>> scanRows(String startRow,
			String stopRow, Set<String> qualifiers) {
		HTableInterface table = pool.getTable(tableName);

		List<Map<String, Map<String, Number>>> ms = new ArrayList<Map<String, Map<String, Number>>>();
		Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
		scan.setCaching(scanCaching);

		ResultScanner rs = null;

		try {
			rs = table.getScanner(scan);

			for (Result result : rs) {
				ms.add(HBaseUtil.reflectRow(result.list(), qualifiers));
			}
		} catch (IOException e) {
			log.error("scan bean from hbase error", e);
		} finally {
			rs.close();
			HBaseUtil.putTableOrClose(pool, table);
		}

		return ms;
	}

	public <T> T getBean(Get get, Class<T> clazz) {
		T t = null;
		try {
			Result result = get(get);
			List<KeyValue> list = result.list();
			t = HBaseUtil.reflectRow(list, clazz);
		} catch (Exception e) {
			log.error("get bean occur exception", e);
		}

		return t;
	}

	public <T> T getBean(String row, Class<T> clazz) {
		Get get = new Get(Bytes.toBytes(row));
		return getBean(get, clazz);
	}

	public HTablePool getPool() {
		return pool;
	}

	public void setPool(HTablePool pool) {
		this.pool = pool;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getScanCaching() {
		return scanCaching;
	}

	public void setScanCaching(int scanCaching) {
		this.scanCaching = scanCaching;
	}

}
