
<template>
    <el-row>
        <el-col :span="2"> <el-button plain type="primary" @click="saveHtmlAndData">保存数据&文档</el-button></el-col>
    </el-row>
    <Source src="/code/SaveDoc.vue"></Source>
    <EditorDemo @AfterInit="onAfterInit" doc="https://www.x-emr.cn/doc/999.html" style="margin: 10px 0;"></EditorDemo>

</template>
    
<script>
    import EditorDemo from "@/components/EditorDemo.vue";

    export default{
      components: {EditorDemo},
        data(){
            return{
                editor:null,
                 //服务端地址
                serverUrl:'http://localhost/post'
            }
        },
        methods:{
            //初始化前
            onAfterInit: function(e) {
                this.editor = e.editor
            },

            //保存文档及机构化数据
            saveHtmlAndData: function() {
                let data = {
                    'doc': this.editor.getHtml(),
                    'data': this.editor.getBindObject()
                }

              const blob = new Blob([data.doc], { type: 'text/html' }); // 指定类型为HTML

              // 创建一个下载链接
              const url = window.URL.createObjectURL(blob);

              // 创建一个链接元素
              const link = document.createElement('a');
              link.href = url;

              // 设置文件名为doc.html
              link.setAttribute('download', 'res.doc');

              // 将链接元素添加到文档中
              document.body.appendChild(link);

              // 模拟用户点击链接进行下载
              link.click();

              // 下载完成后释放URL对象
              window.URL.revokeObjectURL(url);
            },
        }

}
</script>