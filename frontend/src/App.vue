<template>
  <el-container class=container>
      <el-aside class="aside" width="200px">
        <el-menu class="menu" :router=true :default-active="activeLink">
          <el-menu-item index="/">
            <i class="el-icon-suitcase-1"></i>
            <span slot="title">Bin Tools</span>
          </el-menu-item>
          <el-menu-item index="/bin_diff">
            <i class="el-icon-sort"></i>
            <span slot="title">Binary Diff</span>
          </el-menu-item>
          <el-menu-item index="/console_pretty">
            <i class="el-icon-view"></i>
            <span slot="title">Console Pretty</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container> 
        <el-main>
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </el-main>
      </el-container>
    </el-container>
</template>

<script>
export default {
  name: 'app',
  data() {
    return {
      activeLink: null,
    }
  },
  mounted () {
    const debugMode = !!window.webpackHotUpdate;
    if (!debugMode) {
      window.onbeforeunload = function (e) {
        // Cancel the event as stated by the standard.
        event.preventDefault()
        // Chrome requires returnValue to be set.
        event.returnValue = '确定要刷新页面吗？'
        return '确定要刷新页面吗？'
      }
    }
  },
  watch: {
    $route: {
      handler: function(to, from) {
        this.activeLink = to.path;
      },
      deep: true,
      immediate: true
    } 
  }
}
</script>

<style>
.container {
  padding: 10px;
  height: 100vh;
}

.el-main {
  padding: 0 10px !important;
}

.aside {
  background-color: rgb(238, 241, 246);

  padding: 10px;
  margin: 0;
}
</style>
