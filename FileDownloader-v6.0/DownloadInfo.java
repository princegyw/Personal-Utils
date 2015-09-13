package painkiller.multithread_download;

public class DownloadInfo {
	public int task_id;
	public int thread_id;
	public int start_pos;
	public int end_pos;
	public int cur_pos;
	public int seg_size;
	public int complete_size;
	public int is_done;
	public String url;
	
	public DownloadInfo(int task_id, int thread_id,
						int start_pos, int end_pos,
						int cur_pos, int seg_size, 
						int complete_size, 
						int is_done, String url) {
		this.task_id = task_id;
		this.thread_id = thread_id;
		this.start_pos = start_pos;
		this.end_pos = end_pos;
		this.cur_pos = cur_pos;
		this.seg_size = seg_size;
		this.complete_size = complete_size;
		this.is_done = is_done;
		this.url = url;
	}
	
	public String toString()
	{
		return "DownloadInfo[taskId=" + task_id + ", threadId=" +  thread_id
				+ ", startPos=" + start_pos + ", endPos=" + end_pos + ", curPos=" + cur_pos
				+ ", seg_size=" + seg_size +", complete=" + complete_size + ", isDone=" + is_done
				+ ", url=" + url + "]";
	}
}
