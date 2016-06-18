package com.example.cherubim.FileManager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {

    String rootpath;
    // 显示操作菜单
    String[] menu = {"打开", "重命名", "删除", "复制", "剪切","分享"};
    private File currentDirectory = new File("/");

    private List<IconifiedText> directoryEntries;

    private File myTmpFile = null;
    private int myTmpOpt = -1;
    private Boolean isExit = false;
    private long exitTime = 0;


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
   /* public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }

        }
        return true;
    }*/

    //文件过滤器，过滤掉以"."开头的文件
    class MyFilter implements FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.getName().startsWith("."))
                return false;
            else
                return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryEntries = new ArrayList<IconifiedText>();
        browseToRoot();
        this.setSelection(0);
    }

    // 浏览文件系统的根目录
    //浏览根目录
    private void browseToRoot() {
     rootpath = Environment.getExternalStorageDirectory().toString();
        browseTo(
                new File(rootpath));
    }

    //打开文件
    public void openFile(File myFile) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(myFile.getAbsolutePath());
        // 取得文件名
        String fileName = file.getName();

        // 根据不同的文件类型来打开文件
        if (checkEndsWithInStringArray(fileName,
                getResources().getStringArray(R.array.fileEndingImage))) {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingAudio))) {
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
        } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo))) {
            intent.setDataAndType(Uri.fromFile(file), "video/*");
        }
        startActivity(intent);


    }

    //listView源

    private void filiation(File[] files) {
        // 清空列表
        this.directoryEntries.clear();

        // 添加一个当前目录的选项
        this.directoryEntries.add(new IconifiedText(getString(R.string.renovate),
                getResources().getDrawable(R.mipmap.update)));


        // 如果不是根目录则添加上一级目录项0
        if (this.currentDirectory.getParent() != null)
            this.directoryEntries.add(new IconifiedText(
                    getString(R.string.up_one_level), getResources()
                    .getDrawable(R.mipmap.uponlevel)));

        Drawable currentIcon = null;

        for (File currentFile : files) {    // 判断是一个文件夹还是一个文件
            if (currentFile.isDirectory()) {
                currentIcon = getResources().getDrawable(R.mipmap.format_folder);
            } else {
                // 取得文件名
                String fileName = currentFile.getName();
                // 根据文件名来判断文件类型，设置不同的图标
                if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingImage))) {
                    currentIcon = getResources().getDrawable(R.mipmap.format_picture);
                } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingWebText))) {

                    currentIcon = getResources().getDrawable(R.mipmap.format_html);
                } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingPackage))) {
                    currentIcon = getResources().getDrawable(R.mipmap.format_zip);
                } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingAudio))) {
                    currentIcon = getResources().getDrawable(R.mipmap.format_music);
                } else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo))) {
                    currentIcon = getResources().getDrawable(R.mipmap.format_media);
                } else {
                    currentIcon = getResources().getDrawable(R.mipmap.format_text);
                }
            }    // 确保只显示文件名、不显示路径如：/sdcard/111.txt 就只是显示 111.txt
            int currentPathStringLenght = 0;
            if (this.currentDirectory.getParent() != null) {
                currentPathStringLenght = GetCurDirectory().length();
            } else {
                currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
            }

            this.directoryEntries.add(new IconifiedText((currentFile.getAbsolutePath()).substring(currentPathStringLenght), currentIcon));

        }
        Collections.sort(this.directoryEntries);
        IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
        // 将表设置到 ListAdapter 中
        itla.setListItems(this.directoryEntries);
        // 为 ListActivity 添加一个 ListAdapter
        this.setListAdapter(itla);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //upgradeRootPermission(getPackageCodePath());
        // 取得选中的一项的文件名
        String selectedFileString = this.directoryEntries.get(position).getText();

        if (selectedFileString.equals(getString(R.string.renovate))) {
            // 如果选中的是刷新
            this.browseTo(this.currentDirectory);
        } else if (selectedFileString.equals(getString(R.string.up_one_level))) {
            // 返回上一级目录
            this.upOneLevel();
        } else {

            File clickedFile = new File(this.currentDirectory.getAbsolutePath() + "/" + this.directoryEntries.get(position).getText());
            if (clickedFile != null) this.browseTo(clickedFile);
            else {

                Toast.makeText(this, "目录不存在", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 通过文件名判断是什么类型的文件
    private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd)) return true;
        }
        return false;
    }


    //菜单上的按钮
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 2, "新建目录").setIcon(R.mipmap.newfolder);
        menu.add(0, 1, 1, "删除目录").setIcon(R.mipmap.delete);
        menu.add(0, 2, 3, "粘贴文件").setIcon(R.mipmap.paste);
        menu.add(0, 3, 4, "根目录").setIcon(R.mipmap.format_folder);
        menu.add(0, 4, 0, "上一级").setIcon(R.mipmap.uponlevel);
        menu.add(0, 5, 5, "退出").setIcon(R.mipmap.exit);
        return true;
    }


    @Override
    //菜单事件
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                Mynew();
                break;
            case 1:    // 注意：删除目录，谨慎操作，该例子提供了
                // deleteFile（删除文件）和 deleteFolder（删除整个目录）
                MyDelete();
                break;
            case 2:
                MyPaste();
                break;
            case 3:
                this.browseToRoot();
                break;
            case 4:
                this.upOneLevel();
                break;
            case 5:
                this.finish();
                System.exit(0);
        }
        return false;

    }
    //按下按钮事件

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.currentDirectory.getParent() != null) {

                this.upOneLevel();
            } else {
                //exitBy2Click();
                //更为简洁的双击退出方法
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return true;
            }

        }
        return false;//返回false可以继续执行Menu弹出菜单！！！否则菜单失效
    }
    //返回按钮返回上一级而不是退出
    //双击退出的另一种实现

    /*private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }*/

   /* @Override
    public void onBackPressed() {
        this.upOneLevel();

    }*/

    //粘贴
    public void MyPaste() {
        if (myTmpFile == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("没有复制或剪切操作");
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        } else {
            if (myTmpOpt == 0)
            // 复制操作
            {
                if (new File(GetCurDirectory() + "/" + myTmpFile.getName()).exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("粘贴提示");
                    builder.setMessage("该目录有相同的文件，是否需要覆盖？");
                    builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            copyFile(myTmpFile, new File(GetCurDirectory() + myTmpFile.getName()));
                            browseTo(new File(GetCurDirectory()));
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.create();
                    builder.show();
                } else {
                    copyFile(myTmpFile, new File(GetCurDirectory() + myTmpFile.getName()));
                    browseTo(new File(GetCurDirectory()));
                }
            } else if (myTmpOpt == 1)
            // 粘贴操作
            {
                if (new File(GetCurDirectory() + "/" + myTmpFile.getName()).exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("粘贴提示");
                    builder.setMessage("该目录有相同的文件，是否需要覆盖？");
                    builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            moveFile(myTmpFile.getAbsolutePath(), GetCurDirectory() + "/" + myTmpFile.getName());
                            browseTo(new File(GetCurDirectory()));
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.create();
                    builder.show();
                } else {
                    moveFile(myTmpFile.getAbsolutePath(), GetCurDirectory() + "/" + myTmpFile.getName());
                    browseTo(new File(GetCurDirectory()));
                }
            }
        }
    }

    //移动
    public void moveFile(String source, String destination) {
        new File(source).renameTo(new File(destination));
    }

    public void shareFile(File mFile) {


        Intent share = new Intent(Intent.ACTION_SEND);
        Uri uri=Uri.fromFile(mFile);
        share.setType("*/*");//此处可发送多种文件
        share.putExtra(Intent.EXTRA_STREAM,uri);


        startActivity(share);

    }


    //删除
    public void MyDelete() {
        // 取得当前目录
        File tmp = new File(this.currentDirectory.getAbsolutePath());
        // 跳到上一级目录
        this.upOneLevel();
        // 删除取得的目录
        if (deleteFolder(tmp)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("删除成功");
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("删除失败");
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        }
        this.browseTo(this.currentDirectory);
    }

    // 新建文件夹
    public void Mynew() {
        final LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View dialogview = factory.inflate(R.layout.dialog, null);
        // 设置 TextView
        ((TextView) dialogview.findViewById(R.id.TextView_PROM)).setText("请输入新建文件夹的名称！");
        // 设置 EditText
        ((EditText) dialogview.findViewById(R.id.EditText_PROM)).setText("文件夹名称...");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("新建文件夹");
        builder.setView(dialogview);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String value = ((EditText) dialogview.findViewById(R.id.EditText_PROM)).getText().toString();
                if (newFolder(value)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("新建文件夹成功");
                    builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击确定按钮之后,继续执行网页中的 操作
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.create();
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("新建文件夹失败");
                    builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击确定按钮之后,继续执行网页中的 操作
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.create();
                    builder.show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    //新建文件夹
    public boolean newFolder(String file) {
        File dirFile = new File(this.currentDirectory.getAbsolutePath() + "/" + file);
        try {
            if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
                boolean creadok = dirFile.mkdirs();
                if (creadok) {
                    this.browseTo(this.currentDirectory);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return false;
        }
        return true;
    }

    //
    public boolean deleteFolder(File folder) {
        boolean result = false;
        try {
            String childs[] = folder.list();
            if (childs == null || childs.length <= 0) {
                if (folder.delete()) {
                    result = true;
                }
            } else {
                for (int i = 0; i < childs.length; i++) {
                    String childName = childs[i];
                    String childPath = folder.getPath() + File.separator + childName;
                    File filePath = new File(childPath);
                    if (filePath.exists() && filePath.isFile()) {
                        if (filePath.delete()) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    } else if (filePath.exists() && filePath.isDirectory()) {
                        if (deleteFolder(filePath)) {
                            result = true;
                        } else {
                            result = false;
                            break;
                        }
                    }
                }
                folder.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    //得到文件绝对路径

    public String GetCurDirectory() {
        return this.currentDirectory.getAbsolutePath() + "/";
    }

    //删除文件

    public boolean deleteFile(File file) {
        boolean result = false;
        if (file != null) {
            try {
                File file2 = file;
                file2.delete();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    //处理文件
    public void fileOptMenu(final File file) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    openFile(file);
                } else if (which == 1) {
                    //弹出修改对话框
                    LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

                    final View view = layoutInflater.inflate(R.layout.activity_main, null);
                    TextView textView01 = (TextView) view.findViewById(R.id.TextView01);
                    textView01.setText("输入名称");
                    //文件名
                    EditText editText01 = (EditText) view.findViewById(R.id.EditText01);
                    editText01.setText(file.getName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("重命名");
                    builder.setView(view);
                    //确认按钮
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //设置修改后的名称
                            String name = GetCurDirectory() + "/" + ((EditText) view.findViewById(R.id.EditText01)).getText().toString();

                            if (new File(name).exists()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("重命名");
                                builder.setMessage("文件名重复，是否需要覆盖？");
                                builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String str2 = GetCurDirectory() + "/" + ((EditText) view.findViewById(R.id.EditText01)).getText().toString();
                                        file.renameTo(new File(str2));
                                        browseTo(new File(GetCurDirectory()));
                                    }


                                });
                                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }


                                });
                                builder.create();
                                builder.show();
                            } else {
                                file.renameTo(new File(name));
                                browseTo(new File(GetCurDirectory()));
                            }

                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (which == 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("删除文件");
                    builder.setMessage("确定删除" + file.getName() + "？");
                    builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (deleteFile(file)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("提示对话框");
                                builder.setMessage("删除成功");
                                builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int which) {
                                        // 点击确定按钮之后
                                        dialog.cancel();
                                        browseTo(new File(GetCurDirectory()));
                                    }
                                });
                                builder.setCancelable(false);
                                builder.create();
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("提示对话框");
                                builder.setMessage("删除失败");
                                builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击确定按钮之后
                                        dialog.cancel();
                                    }
                                });
                                builder.setCancelable(false);
                                builder.create();
                                builder.show();
                            }


                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create();
                    builder.show();
                } else if (which == 3)
                // 复制
                {      // 保存我们复制的文件目录
                    myTmpFile = file;
                    // 这里我们用 0 表示复制操作
                    myTmpOpt = 0;
                } else if (which == 4)// 剪切
                {      // 保存我们复制的文件目录
                    myTmpFile = file;
                    // 这里我们用 1 表示剪切操作
                    myTmpOpt = 1;
                } else if (which == 5) {
                    shareFile(file);
                }
            }
        };
        new AlertDialog.Builder(MainActivity.this).setTitle("请选择要进行的操作").setItems(menu, listener).show();
    }


    private void browseTo(final File file) {
        if (!file.exists()) {

            Log.i("sgsg", "gsegs");
        } else {
            this.setTitle(file.getAbsolutePath());

            if (file.isDirectory()) {
                this.currentDirectory = file;
                filiation(file.listFiles());
            } else {
                fileOptMenu(file);


            }
        }
    }


    //返回上一级目录
    private void upOneLevel() {
        if (this.currentDirectory.getParent() != null)
            this.browseTo(this.currentDirectory.getParentFile());
    }

    //复制文件
    public void copyFile(File src, File target) {
        InputStream in = null;
        OutputStream out = null;

        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(target);
            bin = new BufferedInputStream(in);
            bout = new BufferedOutputStream(out);

            byte[] b = new byte[8192];
            int len = bin.read(b);
            while (len != -1) {
                bout.write(b, 0, len);
                len = bin.read(b);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bin != null) {
                    bin.close();
                }
                if (bout != null) {
                    bout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
