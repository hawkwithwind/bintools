<template>
    <div>
        <div class="original">
            <el-input type="textarea" v-model="originalLog" placeholder="等待输入原始 log" :rows="10"></el-input>
            <div class="action">
                <el-button class="pretty-button" type="primary" @click="pretty">pretty</el-button>
                <el-button type="primary" v-clipboard:copy="prettyLog" v-clipboard:success="onCopy" v-clipboard:error="onError">copy</el-button>
                <div class="space"></div>
                <el-button @click="clear">clear</el-button>
            </div>
        </div>

        <div class="pretty">
            <textarea id="pretty_text" v-model="prettyLog" placeholder="等待输出 pretty log" disabled></textarea>
        </div>
    </div>
</template>

<script>

export default {
    name: 'iPadConsolePretty',
    data() {
        return {
            originalLog: "",
            prettyLog: ""
        }
    },
    methods: {
        autoHeight() {
            setTimeout(function(){
                const prettyTextEl = document.querySelector("#pretty_text");
                prettyTextEl.style.cssText = 'height:' + prettyTextEl.scrollHeight + 'px';
            },0);
        },
        pretty() {
            let prettyLog = "";
            const lines = this.originalLog.split('\n');

            let currentSeq = 0;
            let currentTime = null;
            let currentContent = null;

            lines.forEach((line) => {
                // Console.app log format
                if (line.indexOf("<Notice>") === -1) {
                    const fields = line.split('\t');

                    if (fields.length < 4) {
                        console.error("illegal line, ignore: ", line);
                        return;
                    }

                    const newLine = fields.filter((_, index) => {
                        return [0, 1].indexOf(index) === -1;
                    }).map((val, index) => {
                        return index === 0 ? val.replace("+0800", "") : val;
                    }).join(" ");

                    const XNSLogOutputStartRegex = /(.*?) -(\d+)- START/g;
                    const startExec = XNSLogOutputStartRegex.exec(newLine);
                    if (startExec) {
                        currentTime = startExec[1];
                        currentSeq = startExec[2];
                        currentContent = "";
                        return;
                    }

                    const XNSLogOutputEndRegex = /(.*?) -(\d+)- END/g;
                    const endExec = XNSLogOutputEndRegex.exec(newLine);
                    if (endExec) {
                        if (endExec[2] !== currentSeq) {
                            console.error("XNSLog different seq, the log maybe not well paired END with START");
                        }

                        prettyLog += `${currentTime} ${currentContent}\n`;
                        currentSeq = 0;
                        currentTime = null;
                        currentContent = null;
                        return;
                    }

                    const XNSLogOutputMiddleRegex = new RegExp("^.*? -" + currentSeq + "- (.*)$");
                    const middleExec = XNSLogOutputMiddleRegex.exec(newLine);
                    if (middleExec) {
                        currentContent += middleExec[1];
                        return;
                    }

                    prettyLog += newLine + '\n';
                }

                // idevicesyslog log format
                else {
                    const fields = line.split(" ");
                    if (fields.length < 6) {
                        console.error("illegal line, ignore: ", line);
                        return;
                    }

                    const newLine = fields.filter((_, index) => {
                        return [3, 4, 5].indexOf(index) === -1;
                    }).join(" ");

                    const XNSLogOutputStartRegex = /(.*?) -(\d+)- START/g;
                    const startExec = XNSLogOutputStartRegex.exec(newLine);
                    if (startExec) {
                        currentTime = startExec[1];
                        currentSeq = startExec[2];
                        currentContent = "";
                        return;
                    }

                    const XNSLogOutputEndRegex = /(.*?) -(\d+)- END/g;
                    const endExec = XNSLogOutputEndRegex.exec(newLine);
                    if (endExec) {
                        if (endExec[2] !== currentSeq) {
                            console.error("XNSLog different seq, the log maybe not well paired END with START");
                        }

                        prettyLog += `${currentTime} ${currentContent}\n`;
                        currentSeq = 0;
                        currentTime = null;
                        currentContent = null;
                        return;
                    }

                    const XNSLogOutputMiddleRegex = new RegExp("^.*? -" + currentSeq + "- (.*)$");
                    const middleExec = XNSLogOutputMiddleRegex.exec(newLine);
                    if (middleExec) {
                        currentContent += middleExec[1];
                        return;
                    }

                    prettyLog += newLine + '\n';
                }
            });

            this.prettyLog = prettyLog;

            this.autoHeight();
        },
        onCopy: function (e) {
            this.$message({
                message: '复制成功',
                type: 'success'
            });
        },
        onError: function (e) {
            this.$message({
                message: '复制失败',
                type: 'warning'
            });
        },
        clear() {
            this.originalLog = "";
            this.prettyLog = "";
            this.autoHeight();
        }
    }
}
</script>

<style scoped lang="scss">
::v-deep .el-textarea__inner {
    font-size: 11px;
    font-family: Consolas,Monaco,Lucida Console,Liberation Mono,DejaVu Sans Mono,Bitstream Vera Sans Mono,Courier New;
    white-space: pre;
}

#pretty_text {
    font-size: 11px;
    font-family: Consolas,Monaco,Lucida Console,Liberation Mono,DejaVu Sans Mono,Bitstream Vera Sans Mono,Courier New;
    white-space: pre;
    color: #313131;

    padding: 5px 15px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    overflow-y: hidden;
}

.original {
    display: flex;
    flex-direction: column;

    .title {
        margin-bottom: 10px;
    }
    .action {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        margin-top: 10px;

        .pretty-button {
            margin-right: 20px;
        }

        .space {
            flex-grow: 1;
        }
    }
}

.pretty {
    display: flex;
    flex-direction: column;
    margin-top: 20px;

    .title {
        margin-bottom: 10px;
    }
}
</style>
