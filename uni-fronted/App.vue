<script>
import { isLoggedIn } from './api/user'

export default {
	onLaunch: function() {
		console.log('App Launch')
		this.checkLoginStatus()
	},
	
	onShow: function() {
		console.log('App Show')
	},
	
	onHide: function() {
		console.log('App Hide')
	},

	methods: {
		/**
		 * 检查登录状态
		 */
		checkLoginStatus() {
			if (!isLoggedIn()) {
				// 没有登录，跳转到登录页
				uni.reLaunch({
					url: '/pages/login/index'
				})
			} else {
				// 已登录，跳转到首页
				uni.reLaunch({
					url: '/pages/income/index'
				})
			}
				// uni.reLaunch({
				// 	url: '/pages/income/index'
				// })
		}
	}
}
</script>

<style>
	/* 每个页面公共css */
	page {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
			'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
			sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}

	button {
		font-family: inherit;
	}

	input,
	textarea {
		font-family: inherit;
	}
</style>
