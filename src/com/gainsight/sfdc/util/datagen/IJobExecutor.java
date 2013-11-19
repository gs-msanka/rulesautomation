package com.gainsight.sfdc.util.datagen;

import java.io.IOException;
import java.sql.SQLException;

public interface IJobExecutor {

	public void init() throws IOException;

	public void getListOfJobs();

	public void execute(JobInfo jobInfo) throws IOException, SQLException;
}
