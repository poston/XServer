package org.xserver.component.hbase;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;

/**
 * Interface specifying a basic set of HBase operations. Implemented by
 * {@link HBaseTemplate}, it's inspired by JdbcOperations.
 * 
 * @author postonzhang
 * @since 2013/08/05
 */
public interface HBaseOperations {
	/**
	 * Put a single row to HBase
	 * 
	 * @param put
	 * @throws IOException
	 */
	public void put(Put put) throws IOException;

	/**
	 * Put a list of rows to HBase
	 * 
	 * @param puts
	 * @throws IOException
	 */
	public void putBatch(List<Put> puts) throws IOException;

	/**
	 * Get a single row from HBase
	 * 
	 * @param get
	 * @return a row in HBase
	 * @throws IOException
	 */
	public Result get(Get get) throws IOException;

	/**
	 * Get a list of rows from HBase
	 * 
	 * @param gets
	 * @return list of rows
	 * @throws IOException
	 */
	public Result[] getBatch(List<Get> gets) throws IOException;

	/**
	 * Delete a single row
	 * 
	 * @param delete
	 * @throws IOException
	 */
	public void delete(Delete delete) throws IOException;

	/**
	 * Delete a list of rows from HBase
	 * 
	 * @param deletes
	 * @throws IOException
	 */
	public void deleteBatch(List<Delete> deletes) throws IOException;

	/**
	 * Execute batch action
	 * 
	 * @param actions
	 *            need to do actions
	 * @param results
	 *            the results of actions
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void batch(List<Row> actions, Object[] results) throws IOException,
			InterruptedException;

	/**
	 * Execute batch action
	 * 
	 * @param actions
	 *            need to do actions
	 * @return the results of actions
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Object[] batch(List<Row> actions) throws IOException,
			InterruptedException;

	/**
	 * Execute scan action
	 * 
	 * @param startRow
	 * @param stopRow
	 * @return
	 * @throws IOException
	 */
	public ResultScanner scan(String startRow, String stopRow)
			throws IOException;
}
