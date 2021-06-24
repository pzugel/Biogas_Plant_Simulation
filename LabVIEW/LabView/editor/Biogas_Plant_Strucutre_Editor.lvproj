<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="19008000">
	<Item Name="My Computer" Type="My Computer">
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Biogas_Plant_Structure_Editor" Type="Folder">
			<Item Name="Elements" Type="Folder">
				<Item Name="feedback.vi" Type="VI" URL="../../elements/feedback.vi"/>
				<Item Name="hydrolyse_reactor.vi" Type="VI" URL="../../elements/hydrolyse_reactor.vi"/>
				<Item Name="hydrolyse_reactor_parallel.vi" Type="VI" URL="../../elements/hydrolyse_reactor_parallel.vi"/>
				<Item Name="methane_reactor.vi" Type="VI" URL="../../elements/methane_reactor.vi"/>
				<Item Name="storage.vi" Type="VI" URL="../../elements/storage.vi"/>
			</Item>
			<Item Name="Structures" Type="Folder">
				<Item Name="STRUCT.vit" Type="VI" URL="../../structures/STRUCT.vit"/>
				<Item Name="STRUCT_1_STAGE.vi" Type="VI" URL="../../structures/STRUCT_1_STAGE.vi"/>
				<Item Name="STRUCT_2_STAGE.vi" Type="VI" URL="../../structures/STRUCT_2_STAGE.vi"/>
				<Item Name="STRUCT_2_STAGE_PL.vi" Type="VI" URL="../../structures/STRUCT_2_STAGE_PL.vi"/>
				<Item Name="STRUCT_3_STAGE.vi" Type="VI" URL="../../structures/STRUCT_3_STAGE.vi"/>
				<Item Name="STRUCT_3_STAGE_PL.vi" Type="VI" URL="../../structures/STRUCT_3_STAGE_PL.vi"/>
				<Item Name="STRUCT_SIMPLE_HYDROLYSIS.vi" Type="VI" URL="../../structures/STRUCT_SIMPLE_HYDROLYSIS.vi"/>
			</Item>
		</Item>
		<Item Name="write_png.vi" Type="VI" URL="../write_png.vi"/>
		<Item Name="Dependencies" Type="Dependencies">
			<Item Name="vi.lib" Type="Folder">
				<Item Name="Application Directory.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Application Directory.vi"/>
				<Item Name="Check Color Table Size.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Check Color Table Size.vi"/>
				<Item Name="Check Data Size.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Check Data Size.vi"/>
				<Item Name="Check File Permissions.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Check File Permissions.vi"/>
				<Item Name="Check Path.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Check Path.vi"/>
				<Item Name="Check if File or Folder Exists.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Check if File or Folder Exists.vi"/>
				<Item Name="Directory of Top Level VI.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Directory of Top Level VI.vi"/>
				<Item Name="Error Cluster From Error Code.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Cluster From Error Code.vi"/>
				<Item Name="Get File Extension.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Get File Extension.vi"/>
				<Item Name="Get System Directory.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/sysdir.llb/Get System Directory.vi"/>
				<Item Name="NI_FileType.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/lvfile.llb/NI_FileType.lvlib"/>
				<Item Name="NI_PackedLibraryUtility.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/LVLibp/NI_PackedLibraryUtility.lvlib"/>
				<Item Name="Space Constant.vi" Type="VI" URL="/&lt;vilib&gt;/dlg_ctls.llb/Space Constant.vi"/>
				<Item Name="System Directory Type.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/sysdir.llb/System Directory Type.ctl"/>
				<Item Name="System Exec.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/system.llb/System Exec.vi"/>
				<Item Name="Write PNG File.vi" Type="VI" URL="/&lt;vilib&gt;/picture/png.llb/Write PNG File.vi"/>
				<Item Name="imagedata.ctl" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/imagedata.ctl"/>
			</Item>
			<Item Name="feedback_run.vi" Type="VI" URL="../../subVI/feedback_run.vi"/>
			<Item Name="hydrolysis_reactor.vi" Type="VI" URL="../../elements/hydrolysis_reactor.vi"/>
			<Item Name="hydrolysis_reactor_parallel.vi" Type="VI" URL="../../elements/hydrolysis_reactor_parallel.vi"/>
			<Item Name="hydrolysis_reactor_run.vi" Type="VI" URL="../../subVI/hydrolysis_reactor_run.vi"/>
			<Item Name="hydrolysis_reactor_run_parallel.vi" Type="VI" URL="../../subVI/hydrolysis_reactor_run_parallel.vi"/>
			<Item Name="hydrolysis_reactor_setup.vi" Type="VI" URL="../../subVI/hydrolysis_reactor_setup.vi"/>
			<Item Name="libCheckpointUpdate.lvlib" Type="Library" URL="../../lib/libCheckpointUpdate/libCheckpointUpdate.lvlib"/>
			<Item Name="libFileFunctions.lvlib" Type="Library" URL="../../lib/libFileFunctions/libFileFunctions.lvlib"/>
			<Item Name="libSimulationControl.lvlib" Type="Library" URL="../../lib/libSimulationControl/libSimulationControl.lvlib"/>
			<Item Name="main_kill_ugshell.vi" Type="VI" URL="../../subVI/main_kill_ugshell.vi"/>
			<Item Name="methane_reactor_run.vi" Type="VI" URL="../../subVI/methane_reactor_run.vi"/>
			<Item Name="methane_reactor_setup.vi" Type="VI" URL="../../subVI/methane_reactor_setup.vi"/>
			<Item Name="methane_reactor_update.vi" Type="VI" URL="../../subVI/methane_reactor_update.vi"/>
			<Item Name="reactor_merge.vi" Type="VI" URL="../../subVI/reactor_merge.vi"/>
			<Item Name="storage_run.vi" Type="VI" URL="../../subVI/storage_run.vi"/>
		</Item>
		<Item Name="Build Specifications" Type="Build"/>
	</Item>
</Project>
