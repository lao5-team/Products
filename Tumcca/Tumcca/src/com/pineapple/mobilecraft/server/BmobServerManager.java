package com.pineapple.mobilecraft.server;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.*;

import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.utils.SyncCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/9.
 */
public class BmobServerManager extends MyServerManager {

    protected static BmobServerManager mCurrInstance = null;

    protected BmobServerManager()
    {

    }

    public static BmobServerManager getInstance()
    {
        if(mCurrInstance == null)
        {
            mCurrInstance = new BmobServerManager();
        }
        return (BmobServerManager)mCurrInstance;
    }


    public MyUser getUserInfo(String username) {
        return null;
    }

    public List<String> getActivityIdsByType(String type)
    {
        final String fType = type;
        SyncCallback<List<String>> callback = new SyncCallback<List<String>>() {

            @Override
            public void executeImpl() {
                BmobQuery<ActivityData> query = new BmobQuery<ActivityData>();
                query.addWhereEqualTo("mJewelType", fType);
                query.findObjects(DemoApplication.applicationContext, new FindListener<ActivityData>() {
                    @Override
                    public void onSuccess(List<ActivityData> object) {
                        List<String> ids = new ArrayList<String>();
                        for(ActivityData data:object)
                        {
                            ids.add(data.getObjectId());
                        }
                        onResult(ids);

                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });
            }
        };

        return callback.executeBegin();
    }

    public List<ActivityData> getActivityByIds(List<String> ids)
    {
        final List<String> fIds = ids;
        SyncCallback<List<ActivityData>> callback = new SyncCallback<List<ActivityData>>() {

            @Override
            public void executeImpl() {
                BmobQuery<ActivityData> query = new BmobQuery<ActivityData>();
                query.addWhereContainedIn("objectId", fIds);
                query.findObjects(DemoApplication.applicationContext, new FindListener<ActivityData>() {
                    @Override
                    public void onSuccess(List<ActivityData> object) {
                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });
            }
        };

        return callback.executeBegin();
    }

    public List<String> getAllActivityIds()
    {
        SyncCallback<List<String>> callback = new SyncCallback<List<String>>() {

            @Override
            public void executeImpl() {
                BmobQuery<ActivityData> query = new BmobQuery<ActivityData>();
                query.findObjects(DemoApplication.applicationContext, new FindListener<ActivityData>() {
                    @Override
                    public void onSuccess(List<ActivityData> object) {
                        List<String> ids = new ArrayList<String>();
                        for(ActivityData data:object)
                        {
                            ids.add(data.getObjectId());
                        }
                        onResult(ids);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        //
        return callback.executeBegin();

    }

    @Override
    public String uploadImage(File file) {

        final File fFile = file;
        SyncCallback<String> callback = new SyncCallback<String>()
        {

            @Override
            public void executeImpl() {
                final BmobFile bmobFile = new BmobFile(fFile);
                bmobFile.uploadblock(DemoApplication.applicationContext, new UploadFileListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        //bmobFile.getUrl()---返回的上传文件的地址（不带域名）
                        //bmobFile.getFileUrl(context)--返回的上传文件的完整地址（带域名）
                        //toast("上传文件成功:" + bmobFile.getFileUrl(context));
                        onResult(bmobFile.getFileUrl(DemoApplication.applicationContext));
                    }

                    @Override
                    public void onProgress(Integer value) {
                        // TODO Auto-generated method stub
                        // 返回的上传进度（百分比）
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                        //toast("上传文件失败：" + msg);
                        onResult("null");
                    }
                });
            }
        };

        return callback.executeBegin();

    }


    public String uploadTreasure(final Treasure treasure)
    {
        SyncCallback<String> callback = new SyncCallback<String>() {

            @Override
            public void executeImpl() {
                treasure.save(DemoApplication.applicationContext, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        onResult("Success");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        };
        //
        return callback.executeBegin();
    }

    public List<Treasure> getAllTreasure()
    {
        SyncCallback<List<Treasure>> callback = new SyncCallback<List<Treasure>>() {

            @Override
            public void executeImpl() {
                BmobQuery<Treasure> query = new BmobQuery<Treasure>();
                query.findObjects(DemoApplication.applicationContext, new FindListener<Treasure>() {
                    @Override
                    public void onSuccess(List<Treasure> object) {

                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public List<Treasure> getTreasureIdsByOwner(String username)
    {
        final String name = username;
        SyncCallback<List<Treasure>> callback = new SyncCallback<List<Treasure>>() {

            @Override
            public void executeImpl() {
                BmobQuery<Treasure> query = new BmobQuery<Treasure>();
                query.addWhereEqualTo("mOwnerName", name);
                query.findObjects(DemoApplication.applicationContext, new FindListener<Treasure>() {
                    @Override
                    public void onSuccess(List<Treasure> object) {
                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public List<Treasure> getTreasuresByIds(List<String> ids)
    {
        final List<String> fids = ids;
        SyncCallback<List<Treasure>> callback = new SyncCallback<List<Treasure>>() {

            @Override
            public void executeImpl() {
                BmobQuery<Treasure> query = new BmobQuery<Treasure>();
                query.addWhereContainsAll("objectId", fids);
                query.findObjects(DemoApplication.applicationContext, new FindListener<Treasure>() {
                    @Override
                    public void onSuccess(List<Treasure> object) {

                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public Treasure getTreasureById(final String id)
    {
        SyncCallback<Treasure> callback = new SyncCallback<Treasure>() {

            @Override
            public void executeImpl() {
                BmobQuery<Treasure> query = new BmobQuery<Treasure>();
                query.getObject(DemoApplication.applicationContext, id, new GetListener<Treasure>() {
                    @Override
                    public void onSuccess(Treasure treasure) {
                        onResult(treasure);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        onResult(Treasure.NULL);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public List<Treasure> getUnindentifiedTreasureIds()
    {
        SyncCallback<List<Treasure>> callback = new SyncCallback<List<Treasure>>() {

            @Override
            public void executeImpl() {
                BmobQuery<Treasure> query = new BmobQuery<Treasure>();
                query.addWhereEqualTo("mIsIdentified", Boolean.FALSE);
                query.findObjects(DemoApplication.applicationContext, new FindListener<Treasure>() {
                    @Override
                    public void onSuccess(List<Treasure> object) {
                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    /**
     *发送评论，并且会发送消息
     * 会给宝物的主人发送消息
     * 如果评论replyTo对象不为空，则给reply对象发送消息
     */

    public void sendTreasureComment(final TreasureComment comment)
    {
        SyncCallback<String> callback = new SyncCallback<String>() {

            @Override
            public void executeImpl() {
                comment.save(DemoApplication.applicationContext, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        onResult(comment.getObjectId());
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            }
        };
        final String commentId = callback.executeBegin();

        final Treasure treasure = getTreasureById(comment.mTreasureId);
        if(treasure!=Treasure.NULL)
        {
            SyncCallback<String> callback2 = new SyncCallback<String>() {

                @Override
                public void executeImpl() {
                    if(comment.mType == TreasureComment.TYPE_COMMENT)
                    {
                        treasure.mCommentIds.add(commentId);
                    }
                    else if(comment.mType == TreasureComment.TYPE_IDENTIFY)
                    {
                        treasure.mIdentifies.add(commentId);
                    }
                    treasure.update(DemoApplication.applicationContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            onResult("Success");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            onResult("Failure");
                        }
                    });

                }
            };
            callback2.executeBegin();
        }

    }

    public List<TreasureComment> getTreasureComments(final List<String> ids)
    {
        SyncCallback<List<TreasureComment>> callback = new SyncCallback<List<TreasureComment>>() {

            @Override
            public void executeImpl() {
                BmobQuery<TreasureComment> query = new BmobQuery<TreasureComment>();
                query.addWhereContainedIn("objectId", ids);
                query.findObjects(DemoApplication.applicationContext, new FindListener<TreasureComment>() {
                    @Override
                    public void onSuccess(List<TreasureComment> object) {

                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public List<TreasureComment> getTreasureProfComment(final List<String> ids)
    {
        SyncCallback<List<TreasureComment>> callback = new SyncCallback<List<TreasureComment>>() {

            @Override
            public void executeImpl() {
                BmobQuery<TreasureComment> query = new BmobQuery<TreasureComment>();
                query.addWhereContainedIn("objectId", ids);
                query.findObjects(DemoApplication.applicationContext, new FindListener<TreasureComment>() {
                    @Override
                    public void onSuccess(List<TreasureComment> object) {

                        onResult(object);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        onResult(null);
                    }
                });

            }
        };
        return callback.executeBegin();
    }

    public TreasureComment getTreasureComment(final String id) {
        SyncCallback<TreasureComment> callback = new SyncCallback<TreasureComment>() {

            @Override
            public void executeImpl() {
                BmobQuery<TreasureComment> query = new BmobQuery<TreasureComment>();
                query.getObject(DemoApplication.applicationContext, id, new GetListener<TreasureComment>() {
                    @Override
                    public void onSuccess(TreasureComment data) {
                        onResult(data);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        onResult(TreasureComment.NULL);
                    }
                });

            }
        };
        return callback.executeBegin();
    }
    
    @Override
    public ActivityData getActivity(final String id) {
        SyncCallback<ActivityData> callback = new SyncCallback<ActivityData>() {

            @Override
            public void executeImpl() {
                BmobQuery<ActivityData> query = new BmobQuery<ActivityData>();
                query.getObject(DemoApplication.applicationContext, id, new GetListener<ActivityData>() {
                    @Override
                    public void onSuccess(ActivityData data) {
                        onResult(data);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        onResult(ActivityData.NULL);
                    }
                });

            }
        };
        return callback.executeBegin();
    }
    
    /**
     * 向用户消息表中加入一条数据
     * @param message
     * @return
     */
	public String sendMessage(final TreasureMessage message) {
		SyncCallback<String> callback = new SyncCallback<String>() {

			@Override
			public void executeImpl() {
				message.update(DemoApplication.applicationContext,
						new UpdateListener() {
							@Override
							public void onSuccess() {
								onResult("Success");
							}

							@Override
							public void onFailure(int i, String s) {
								onResult("Failure");
							}
						});

			}
		};
		return callback.executeBegin();
	}

	public String createMessage(final TreasureMessage message) {
		SyncCallback<String> callback = new SyncCallback<String>() {

			@Override
			public void executeImpl() {
				message.save(DemoApplication.applicationContext,
						new SaveListener() {
							@Override
							public void onSuccess() {
								onResult("Success");
							}

							@Override
							public void onFailure(int i, String s) {
								onResult("Failure");
							}
						});

			}
		};
		return callback.executeBegin();
	}	
	/**
	 * 获取用户的所有消息
	 * @param username
	 * @return
	 */
	public TreasureMessage getTreasureMessage(final String username)
	{
        SyncCallback<TreasureMessage> callback = new SyncCallback<TreasureMessage>() {

            @Override
            public void executeImpl() {
                BmobQuery<TreasureMessage> query = new BmobQuery<TreasureMessage>();
                query.addWhereEqualTo("mUsername", username);
                query.findObjects(DemoApplication.applicationContext, new FindListener<TreasureMessage>() {
					
					@Override
					public void onSuccess(List<TreasureMessage> arg0) {
						// TODO Auto-generated method stub
						if(arg0.size()>0)
						{
							onResult(arg0.get(0));
						}
						else
						{
							onResult(TreasureMessage.NULL);
						}
						
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						onResult(TreasureMessage.NULL);
					}
				});

            }
        };
        return callback.executeBegin();		
	}
}
